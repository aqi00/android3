package com.example.chapter20;

import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter20.bean.ContactInfo;
import com.example.chapter20.constant.ChatConst;
import com.example.chapter20.util.BitmapUtil;
import com.example.chapter20.util.ChatUtil;
import com.example.chapter20.util.SocketUtil;
import com.example.chapter20.webrtc.Peer;
import com.example.chapter20.webrtc.ProxyVideoSink;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaConstraints.KeyValuePair;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.audio.AudioDeviceModule;
import org.webrtc.audio.JavaAudioDeviceModule;

import java.util.List;

import io.socket.client.Socket;

public class ContactVideoActivity extends AppCompatActivity {
    private final static String TAG = "ContactVideoActivity";
    private ImageView iv_wait; // 声明一个图像视图对象
    private Chronometer chr_cost; // 声明一个计时器对象
    private LinearLayout ll_left; // 声明一个线性视图对象
    private LinearLayout ll_middle; // 声明一个线性视图对象
    private LinearLayout ll_right; // 声明一个线性视图对象
    private ContactInfo mContact; // 联系信息（联系人昵称与被联系人昵称）
    private AudioManager mAudioManager; // 声明一个音频管理器对象

    private Socket mSocket; // 声明一个套接字对象
    private SurfaceViewRenderer svr_local; // 本地的表面视图渲染器（我方）
    private SurfaceViewRenderer svr_remote; // 远程的表面视图渲染器（对方）
    private PeerConnectionFactory mConnFactory; // 点对点连接工厂
    private EglBase mEglBase; // OpenGL ES 与本地设备之间的接口对象
    private MediaStream mMediaStream; // 媒体流
    private VideoCapturer mVideoCapturer; // 视频捕捉器
    private MediaConstraints mAnswerConstraints; // 应答方的媒体条件
    private MediaConstraints mOfferConstraints; // 提供方的媒体条件
    private MediaConstraints mAudioConstraints; // 音频的媒体条件
    private List<PeerConnection.IceServer> mIceServers = ChatConst.getIceServerList(); // ICE服务器列表
    private Peer mPeer; // 点对点对象
    private boolean isOffer = false; // 是否为提供方（发起方）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_video);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕常亮
        Bundle bundle = getIntent().getExtras();
        String self_name = bundle.getString("self_name");
        String friend_name = bundle.getString("friend_name");
        mContact = new ContactInfo(self_name, friend_name);
        isOffer = bundle.getBoolean("is_offer");
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mAudioManager.setMode(AudioManager.MODE_IN_CALL); // 设置通话模式
        mAudioManager.setSpeakerphoneOn(false); // 是否开启免提
        initRender(); // 初始化双方的渲染图层
        initStream(); // 初始化音视频的媒体流
        initSocket(); // 初始化信令交互的套接字
        initView(); // 初始化视图界面
    }

    // 初始化视图界面
    private void initView() {
        iv_wait = findViewById(R.id.iv_wait);
        chr_cost = findViewById(R.id.chr_cost);
        TextView tv_friend = findViewById(R.id.tv_friend);
        ll_left = findViewById(R.id.ll_left);
        ll_middle = findViewById(R.id.ll_middle);
        ll_right = findViewById(R.id.ll_right);
        ll_left.setOnClickListener(v -> dialOff()); // 挂断通话
        ll_middle.setOnClickListener(v -> dialOff()); // 挂断通话
        if (isOffer) { // 主动提出通话
            tv_friend.setText("邀请" + mContact.to + "来视频通话");
            ll_left.setVisibility(View.INVISIBLE);
            ll_right.setVisibility(View.INVISIBLE);
            SocketUtil.emit(mSocket, "offer_converse", mContact); // 请求与对方通话
            // 等待对方接受视频通话
            mSocket.on("other_dial_in", (args) -> {
                mPeer.getConnection().createOffer(mPeer, mOfferConstraints); // 创建供应
                runOnUiThread(() -> beginConversation()); // 开始视频通话
            });
        } else { // 被动接受通话
            tv_friend.setText(mContact.to + "邀请你视频通话");
            ll_middle.setVisibility(View.INVISIBLE);
            ll_right.setOnClickListener(v -> {
                SocketUtil.emit(mSocket, "self_dial_in", mContact); // 我方同意了视频通话
                beginConversation(); // 开始视频通话
            });
        }
        mSocket.on("other_hang_up", (args) -> dialOff()); // 等待对方挂断通话
        new Handler(Looper.myLooper()).post(() -> showBlurBackground());
    }

    // 显示等待接通时候的模糊背景
    private void showBlurBackground() {
        // 根据昵称获取对应的位图
        Bitmap origin = ChatUtil.getBitmapByName(this, mContact.to);
        Bitmap blur = BitmapUtil.convertBlur(origin); // 获取模糊化的位图
        iv_wait.setImageBitmap(blur); // 设置图像视图的位图对象
    }

    // 开始视频通话
    private void beginConversation() {
        Log.d(TAG, "beginConversation");
        iv_wait.setVisibility(View.GONE);
        ll_left.setVisibility(View.INVISIBLE);
        ll_right.setVisibility(View.INVISIBLE);
        ll_middle.setVisibility(View.VISIBLE);
        chr_cost.setVisibility(View.VISIBLE);
        chr_cost.setBase(SystemClock.elapsedRealtime()); // 设置计时器的基准时间
        chr_cost.start(); // 开始计时
    }

    // 挂断通话
    private void dialOff() {
        mSocket.off("other_hang_up"); // 取消监听对方的挂断请求
        SocketUtil.emit(mSocket, "self_hang_up", mContact); // 发出挂断通话消息
        finish(); // 关闭当前页面
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dialOff(); // 挂断通话
    }

    // 初始化双方的渲染图层
    private void initRender() {
        svr_local = findViewById(R.id.svr_local);
        svr_remote = findViewById(R.id.svr_remote);
        mEglBase = EglBase.create(); // 创建EglBase实例
        // 以下初始化我方的渲染图层
        svr_local.init(mEglBase.getEglBaseContext(), null);
        svr_local.setMirror(true); // 是否设置镜像
        svr_local.setZOrderMediaOverlay(true); // 是否置于顶层
        // 设置缩放类型，SCALE_ASPECT_FILL表示充满视图
        svr_local.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        svr_local.setEnableHardwareScaler(false); // 是否开启硬件缩放
        // 以下初始化对方的渲染图层
        svr_remote.init(mEglBase.getEglBaseContext(), null);
        svr_remote.setMirror(false); // 是否设置镜像
        svr_remote.setZOrderMediaOverlay(false); // 是否置于顶层
        // 设置缩放类型，SCALE_ASPECT_FILL表示充满视图
        svr_remote.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        svr_remote.setEnableHardwareScaler(false); // 是否开启硬件缩放
    }

    // 初始化音视频的媒体流
    private void initStream() {
        Log.d(TAG, "initStream");
        // 初始化点对点连接工厂
        PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(getApplicationContext())
                        .createInitializationOptions());
        // 创建视频的编解码方式
        VideoEncoderFactory encoderFactory;
        VideoDecoderFactory decoderFactory;
        encoderFactory = new DefaultVideoEncoderFactory(
                mEglBase.getEglBaseContext(), true, true);
        decoderFactory = new DefaultVideoDecoderFactory(mEglBase.getEglBaseContext());
        AudioDeviceModule audioModule = JavaAudioDeviceModule.builder(this).createAudioDeviceModule();
        // 创建点对点连接工厂
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        mConnFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setAudioDeviceModule(audioModule)
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();
        initConstraints(); // 初始化视频通话的各项条件
        // 创建音视频的媒体流
        mMediaStream = mConnFactory.createLocalMediaStream("local_stream");
        // 以下创建并添加音频轨道
        AudioSource audioSource = mConnFactory.createAudioSource(mAudioConstraints);
        AudioTrack audioTrack = mConnFactory.createAudioTrack("audio_track", audioSource);
        mMediaStream.addTrack(audioTrack);
        // 以下创建并初始化视频捕捉器
        mVideoCapturer = createVideoCapture();
        VideoSource videoSource = mConnFactory.createVideoSource(mVideoCapturer.isScreencast());
        SurfaceTextureHelper surfaceHelper = SurfaceTextureHelper.create("CaptureThread", mEglBase.getEglBaseContext());
        mVideoCapturer.initialize(surfaceHelper, this, videoSource.getCapturerObserver());
        // 设置视频画质。三个参数分别表示：视频宽度、视频高度、每秒传输帧数fps
        mVideoCapturer.startCapture(720, 1080, 15);
        // 以下创建并添加视频轨道
        VideoTrack videoTrack = mConnFactory.createVideoTrack("video_track", videoSource);
        mMediaStream.addTrack(videoTrack);
        ProxyVideoSink localSink = new ProxyVideoSink();
        localSink.setTarget(svr_local); // 指定视频轨道中我方的渲染图层
        mMediaStream.videoTracks.get(0).addSink(localSink);
    }

    // 初始化信令交互的套接字
    private void initSocket() {
        mSocket = MainApplication.getInstance().getSocket();
        // 等待接入ICE候选者，目的是打通流媒体传输网络
        mSocket.on("IceInfo", args -> {
            Log.d(TAG, "IceInfo");
            try {
                JSONObject json = (JSONObject) args[0];
                IceCandidate candidate = new IceCandidate(json.getString("id"),
                        json.getInt("label"), json.getString("candidate")
                );
                mPeer.getConnection().addIceCandidate(candidate); // 添加ICE候选者
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        // 等待对方的会话连接，以便建立双方的通信链路
        mSocket.on("SdpInfo", args -> {
            Log.d(TAG, "SdpInfo");
            try {
                JSONObject json = (JSONObject) args[0];
                SessionDescription sd = new SessionDescription
                        (SessionDescription.Type.fromCanonicalForm(
                                json.getString("type")), json.getString("description"));
                mPeer.getConnection().setRemoteDescription(mPeer, sd); // 设置对方的会话描述
                if (!isOffer) { // 不是提供方，就给会话连接创建应答
                    mPeer.getConnection().createAnswer(mPeer, mAnswerConstraints);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        // 第四个参数表示对方接受视频通话之后，如何显示对方的视频画面
        mPeer = new Peer(mSocket, mContact.from, mContact.to, (userId, remoteStream) -> {
            ProxyVideoSink remoteSink = new ProxyVideoSink();
            remoteSink.setTarget(svr_remote); // 设置视频轨道中对方的渲染图层
            VideoTrack videoTrack = remoteStream.videoTracks.get(0);
            videoTrack.addSink(remoteSink);
        });
        mPeer.init(mConnFactory, mMediaStream, mIceServers); // 初始化点对点连接
    }

    // 初始化视频通话的各项条件
    private void initConstraints() {
        // 创建应答方的媒体条件
        mAnswerConstraints = new MediaConstraints();
        // 是否采用默认的dtls协议srtp密钥机制
        mAnswerConstraints.optional.add(new KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        // 是否要求最小的数据连接
        mAnswerConstraints.optional.add(new KeyValuePair("RtpDataChannels", "true"));
        // 创建发起方的媒体条件
        mOfferConstraints = new MediaConstraints();
        // 是否接受音频流
        mOfferConstraints.mandatory.add(new KeyValuePair("OfferToReceiveAudio", "true"));
        // 是否接受视频流
        mOfferConstraints.mandatory.add(new KeyValuePair("OfferToReceiveVideo", "true"));
        // 创建音频流的媒体条件
        mAudioConstraints = new MediaConstraints();
        // 是否消除回声
        mAudioConstraints.mandatory.add(new KeyValuePair("googEchoCancellation", "true"));
        // 是否自动增益
        mAudioConstraints.mandatory.add(new KeyValuePair("googAutoGainControl", "true"));
        // 是否过滤高音
        mAudioConstraints.mandatory.add(new KeyValuePair("googHighpassFilter", "true"));
        // 是否抑制噪音
        mAudioConstraints.mandatory.add(new KeyValuePair("googNoiseSuppression", "true"));
    }

    // 根据相机类型创建对应的视频捕捉器
    private VideoCapturer createCameraCapture(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();
        // 先使用前置摄像头
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        // 没有前置摄像头再找后置摄像头
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }

    // 创建视频捕捉器
    private VideoCapturer createVideoCapture() {
        VideoCapturer videoCapturer;
        if (Camera2Enumerator.isSupported(this)) { // 优先使用二代相机
            videoCapturer = createCameraCapture(new Camera2Enumerator(this));
        } else { // 如果不支持二代相机，就使用传统相机
            videoCapturer = createCameraCapture(new Camera1Enumerator(true));
        }
        return videoCapturer;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("other_dial_in"); // 取消监听对方的接入请求
        mSocket.off("other_hang_up"); // 取消监听对方的挂断请求
        mSocket.off("IceInfo"); // 取消监听流媒体传输
        mSocket.off("SdpInfo"); // 取消监听会话连接
        svr_local.release(); // 释放本地的渲染器资源（我方）
        svr_remote.release(); // 释放远程的渲染器资源（对方）
        try { // 停止视频捕捉，也就是关闭摄像头
            mVideoCapturer.stopCapture();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean result = true;
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) { // 调大音量
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL,
                    AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) { // 调小音量
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL,
                    AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        } else {
            result = super.onKeyDown(keyCode, event);
        }
        return result;
    }

}