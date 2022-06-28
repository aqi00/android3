package com.example.chapter20;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chapter20.bean.JoinInfo;
import com.example.chapter20.bean.MessageInfo;
import com.example.chapter20.constant.ChatConst;
import com.example.chapter20.util.ChatUtil;
import com.example.chapter20.util.DialogUtil;
import com.example.chapter20.util.SocketUtil;
import com.example.chapter20.util.ViewUtil;
import com.example.chapter20.webrtc.Peer;
import com.example.chapter20.webrtc.ProxyVideoSink;
import com.example.chapter20.widget.RewardView;
import com.google.gson.Gson;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;

@SuppressLint({"DefaultLocale", "SetTextI18n"})
public class LiveServerActivity extends AppCompatActivity {
    private final static String TAG = "LiveServerActivity";
    private TextView tv_count; // 声明一个文本视图对象
    private EditText et_input; // 声明一个编辑框视图对象
    private LinearLayout ll_message; // 声明一个线性视图对象
    private TextView tv_nickname; // 声明一个文本视图对象
    private ScrollView sv_chat; // 声明一个滚动视图对象
    private LinearLayout ll_show; // 声明一个聊天窗口的线性布局对象
    private String mSelfName, mRoomName; // 我的昵称，房间名称
    private int mPersonCount = 0; // 人员数量
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象

    private Socket mSocket; // 声明一个套接字对象
    private SurfaceViewRenderer svr_content; // 表面视图渲染器
    private PeerConnectionFactory mConnFactory; // 点对点连接工厂
    private EglBase mEglBase; // OpenGL ES 与本地设备之间的接口对象
    private MediaStream mMediaStream; // 媒体流
    private VideoCapturer mVideoCapturer; // 视频捕捉器
    private MediaConstraints mOfferConstraints; // 提供方的媒体条件
    private MediaConstraints mAudioConstraints; // 音频的媒体条件
    private List<PeerConnection.IceServer> mIceServers = ChatConst.getIceServerList(); // ICE服务器列表
    private Map<String, Peer> mPeerMap = new HashMap<>(); // 点对点名称映射

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_cast);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕常亮
        mSelfName = getIntent().getStringExtra("self_name");
        mRoomName = getIntent().getStringExtra("room_name");
        initRender(); // 初始化双方的渲染图层
        initStream(); // 初始化音视频的媒体流
        initSocket(); // 初始化信令交互的套接字
        initView(); // 初始化视图界面
    }

    // 初始化视图界面
    private void initView() {
        ImageView iv_room = findViewById(R.id.iv_room);
        TextView tv_room = findViewById(R.id.tv_room);
        tv_count = findViewById(R.id.tv_count);
        et_input = findViewById(R.id.et_input);
        ll_message = findViewById(R.id.ll_message);
        tv_nickname = findViewById(R.id.tv_nickname);
        sv_chat = findViewById(R.id.sv_chat);
        ll_show = findViewById(R.id.ll_show);
        findViewById(R.id.btn_send).setOnClickListener(v -> sendMessage());
        RewardView rv_gift = findViewById(R.id.rv_gift);
        findViewById(R.id.iv_reward).setOnClickListener(v -> rv_gift.addGiftView());
        ll_message.setVisibility(View.INVISIBLE);
        iv_room.setImageDrawable(ChatUtil.getPortraitByName(this, mRoomName));
        tv_room.setText(mRoomName);
        findViewById(R.id.iv_close).setOnClickListener(v -> finish());
        findViewById(R.id.tv_follow).setOnClickListener(v -> DialogUtil.showDialog(this, "您关注了该店铺"));
        findViewById(R.id.tv_join).setOnClickListener(v -> DialogUtil.showDialog(this, "您已成为店铺会员"));
        findViewById(R.id.tv_discount).setOnClickListener(v -> DialogUtil.showDialog(this, "您已领取店铺红包"));
        findViewById(R.id.tv_order).setOnClickListener(v -> DialogUtil.showDialog(this, "您已在该店铺下单"));
    }

    // 初始化双方的渲染图层
    private void initRender() {
        svr_content = findViewById(R.id.svr_content);
        mEglBase = EglBase.create(); // 创建EglBase实例
        // 以下初始化视频的渲染图层
        svr_content.init(mEglBase.getEglBaseContext(), null);
        svr_content.setMirror(true); // 是否设置镜像
        svr_content.setZOrderMediaOverlay(true); // 是否置于顶层
        // 设置缩放类型，SCALE_ASPECT_FILL表示充满视图
        svr_content.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        svr_content.setEnableHardwareScaler(false); // 是否开启硬件缩放
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
        localSink.setTarget(svr_content); // 指定视频轨道中我方的渲染图层
        mMediaStream.videoTracks.get(0).addSink(localSink);
    }

    // 初始化信令交互的套接字
    private void initSocket() {
        Log.d(TAG, "initSocket");
        mSocket = MainApplication.getInstance().getSocket();
        // 开始监听人员数量统计事件
        mSocket.on("person_count", (args) -> {
            int person_count = (Integer) args[0];
            if (person_count > mPersonCount) {
                mPersonCount = (Integer) args[0];
                runOnUiThread(() -> tv_count.setText(String.format("当前共 %d 人观看", mPersonCount)));
            }
        });
        // 开始监听房间消息接收事件
        mSocket.on("receive_room_message", (args) -> {
            JSONObject json = (JSONObject) args[0];
            MessageInfo message = new Gson().fromJson(json.toString(), MessageInfo.class);
            // 往聊天窗口添加文本消息
            runOnUiThread(() -> appendChatMsg(message.from, message.content, false));
        });
        // 开始监听有人进入房间事件
        mSocket.on("person_in_room", (args) -> {
            runOnUiThread(() -> someoneInRoom((String) args[0])); // 有人进入房间
        });
        // 开始监听有人退出房间事件
        mSocket.on("person_out_room", (args) -> {
            runOnUiThread(() -> someoneOutRoom((String) args[0])); // 有人退出房间
        });
        // 等待接入ICE候选者，目的是打通流媒体传输网络
        mSocket.on("IceInfo", args -> {
            Log.d(TAG, "IceInfo");
            try {
                JSONObject json = (JSONObject) args[0];
                IceCandidate candidate = new IceCandidate(json.getString("id"),
                        json.getInt("label"), json.getString("candidate")
                );
                //Log.d(TAG, "json="+json.toString());
                String source = json.getString("source");
                String destination = json.getString("destination");
                String other_name = source.equals(mSelfName) ? destination : source;
                //Log.d(TAG, "IceInfo other_name="+other_name);
                Peer peer = mPeerMap.get(other_name); // 找到该用户的点对点对象
                peer.getConnection().addIceCandidate(candidate); // 添加ICE候选者
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
                //Log.d(TAG, "json="+json.toString());
                String source = json.getString("source");
                String destination = json.getString("destination");
                String other_name = source.equals(mSelfName) ? destination : source;
                //Log.d(TAG, "IceInfo other_name="+other_name);
                Peer peer = mPeerMap.get(other_name); // 找到该用户的点对点对象
                if (peer != null) {
                    peer.getConnection().setRemoteDescription(peer, sd);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        Log.d(TAG, mSelfName+" emit join_room "+mRoomName);
        // 下面通知服务器已经进入房间
        JoinInfo joinInfo = new JoinInfo(mSelfName, mRoomName);
        SocketUtil.emit(mSocket, "join_room", joinInfo);

        // 等待对方拨号进入直播
        mSocket.on("other_dial_in", (args) -> {
            String other_name = (String) args[0];
            Log.d(TAG, "other_dial_in "+other_name);
            // 第四个参数表示对方拨号进入直播之后，如何显示对方的视频画面
            Peer peer = new Peer(mSocket, mSelfName, other_name, (userId, remoteStream) -> Log.d(TAG, "new Peer"));
            peer.init(mConnFactory, mMediaStream, mIceServers); // 初始化点对点连接
            peer.getConnection().createOffer(peer, mOfferConstraints); // 创建供应
            mPeerMap.put(other_name, peer);
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
        mSocket.emit("close_room", mRoomName); // 通知服务器已经关闭房间
        mSocket.off("person_count"); // 取消监听人员数量统计事件
        mSocket.off("receive_room_message"); // 取消监听房间消息接收事件
        mSocket.off("person_out_room"); // 取消监听有人离开房间事件
        mSocket.off("person_in_room"); // 取消监听有人进入房间事件
        mSocket.off("other_dial_in"); // 取消监听用户的接入请求
        mSocket.off("IceInfo"); // 取消监听流媒体传输
        mSocket.off("SdpInfo"); // 取消监听会话连接
        svr_content.release(); // 释放渲染器资源
        try { // 停止视频捕捉，也就是关闭摄像头
            mVideoCapturer.stopCapture();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 有人进入房间
    private void someoneInRoom(String person_name) {
        Log.d(TAG, person_name+" join room");
        if (!person_name.equals(mSelfName)){
            tv_count.setText(String.format("当前共 %d 人观看", ++mPersonCount));
        }
        ll_message.setVisibility(View.VISIBLE);
        tv_nickname.setText(person_name+"进入了直播间");
        mHandler.postDelayed(() -> ll_message.setVisibility(View.INVISIBLE), 2000);
    }

    // 有人退出房间
    private void someoneOutRoom(String person_name) {
        Log.d(TAG, person_name+" leave room");
        if (!person_name.equals(mSelfName)){
            tv_count.setText(String.format("当前共 %d 人观看", --mPersonCount));
        }
        ll_message.setVisibility(View.VISIBLE);
        tv_nickname.setText(person_name+"离开了直播间");
        mHandler.postDelayed(() -> ll_message.setVisibility(View.INVISIBLE), 2000);
    }

    // 发送聊天消息
    private void sendMessage() {
        String content = et_input.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入聊天消息", Toast.LENGTH_SHORT).show();
            return;
        }
        et_input.setText("");
        ViewUtil.hideOneInputMethod(this, et_input); // 隐藏软键盘
        appendChatMsg(mSelfName, content, true); // 往聊天窗口添加文本消息
        // 下面往服务器发送聊天消息
        MessageInfo message = new MessageInfo(mSelfName, mRoomName, content);
        SocketUtil.emit(mSocket, "send_room_message", message);
    }

    // 往聊天窗口添加聊天消息
    private void appendChatMsg(String name, String content, boolean isSelf) {
        // 把群聊消息的线性布局添加到聊天窗口上
        ll_show.addView(ChatUtil.getSimpleChatView(this, name, content, isSelf));
        // 延迟100毫秒后启动聊天窗口的滚动任务，房间消息滚动到底部
        mHandler.postDelayed(() -> sv_chat.fullScroll(ScrollView.FOCUS_DOWN), 100);
    }

}