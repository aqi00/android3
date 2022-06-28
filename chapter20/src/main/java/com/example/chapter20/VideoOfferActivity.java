package com.example.chapter20;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.chapter20.bean.ContactInfo;
import com.example.chapter20.constant.ChatConst;
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

public class VideoOfferActivity extends AppCompatActivity {
    private final static String TAG = "VideoOfferActivity";
    private Socket mSocket; // 声明一个套接字对象
    private SurfaceViewRenderer svr_local; // 本地的表面视图渲染器（我方）
    private PeerConnectionFactory mConnFactory; // 点对点连接工厂
    private EglBase mEglBase; // OpenGL ES 与本地设备之间的接口对象
    private MediaStream mMediaStream; // 媒体流
    private VideoCapturer mVideoCapturer; // 视频捕捉器
    private MediaConstraints mOfferConstraints; // 提供方的媒体条件
    private MediaConstraints mAudioConstraints; // 音频的媒体条件
    private List<PeerConnection.IceServer> mIceServers = ChatConst.getIceServerList(); // ICE服务器列表
    private Peer mPeer; // 点对点对象
    private ContactInfo mContact = new ContactInfo("提供方", "接收方");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_offer);
        initRender(); // 初始化渲染图层
        initStream(); // 初始化音视频的媒体流
        initSocket(); // 初始化信令交互的套接字
        initView(); // 初始化视图界面
    }

    // 初始化视图界面
    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("这里是视频提供方");
        findViewById(R.id.iv_back).setOnClickListener(v -> dialOff()); // 挂断通话
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

    // 初始化渲染图层
    private void initRender() {
        svr_local = findViewById(R.id.svr_local);
        mEglBase = EglBase.create(); // 创建EglBase实例
        // 以下初始化我方的渲染图层
        svr_local.init(mEglBase.getEglBaseContext(), null);
        svr_local.setMirror(true); // 是否设置镜像
        svr_local.setZOrderMediaOverlay(true); // 是否置于顶层
        // 设置缩放类型，SCALE_ASPECT_FILL表示充满视图
        svr_local.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        svr_local.setEnableHardwareScaler(false); // 是否开启硬件缩放
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
        mSocket.connect(); // 建立Socket连接
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        mSocket.on("other_hang_up", (args) -> dialOff()); // 等待对方挂断通话
        Log.d(TAG, "self_dial_in");
        SocketUtil.emit(mSocket, "self_dial_in", mContact); // 我方发起了视频通话
        // 等待对方接受视频通话
        mSocket.on("other_dial_in", (args) -> {
            String other_name = (String) args[0];
            Log.d(TAG, mContact.from+" to "+mContact.to+", other_name="+other_name);
            // 第四个参数表示对方接受视频通话之后，如何显示对方的视频画面
            mPeer = new Peer(mSocket, mContact.from, mContact.to, (userId, remoteStream) -> Log.d(TAG, "new Peer"));
            mPeer.init(mConnFactory, mMediaStream, mIceServers); // 初始化点对点连接
            mPeer.getConnection().createOffer(mPeer, mOfferConstraints); // 创建供应
        });
    }

    // 初始化视频通话的各项条件
    private void initConstraints() {
        // 创建发起方的媒体条件
        mOfferConstraints = new MediaConstraints();
        // 是否接受音频流
        mOfferConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        // 是否接受视频流
        mOfferConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        // 创建音频流的媒体条件
        mAudioConstraints = new MediaConstraints();
        // 是否消除回声
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googEchoCancellation", "true"));
        // 是否自动增益
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googAutoGainControl", "true"));
        // 是否过滤高音
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googHighpassFilter", "true"));
        // 是否抑制噪音
        mAudioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googNoiseSuppression", "true"));
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
        try { // 停止视频捕捉，也就是关闭摄像头
            mVideoCapturer.stopCapture();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mSocket.connected()) { // 已经连上Socket服务器
            mSocket.disconnect(); // 断开Socket连接
        }
    }

}