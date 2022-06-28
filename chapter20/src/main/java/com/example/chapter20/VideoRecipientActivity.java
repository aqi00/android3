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
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoTrack;
import org.webrtc.audio.AudioDeviceModule;
import org.webrtc.audio.JavaAudioDeviceModule;

import java.util.List;

import io.socket.client.Socket;

public class VideoRecipientActivity extends AppCompatActivity {
    private final static String TAG = "VideoRecipientActivity";
    private Socket mSocket; // 声明一个套接字对象
    private SurfaceViewRenderer svr_remote; // 远程的表面视图渲染器（对方）
    private PeerConnectionFactory mConnFactory; // 点对点连接工厂
    private EglBase mEglBase; // OpenGL ES 与本地设备之间的接口对象
    private MediaStream mMediaStream; // 媒体流
    private List<PeerConnection.IceServer> mIceServers = ChatConst.getIceServerList(); // ICE服务器列表
    private Peer mPeer; // 点对点对象
    private ContactInfo mContact = new ContactInfo("接收方", "提供方");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recipient);
        initRender(); // 初始化渲染图层
        initStream(); // 初始化音视频的媒体流
        initSocket(); // 初始化信令交互的套接字
        initView(); // 初始化视图界面
    }

    // 初始化视图界面
    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("这里是视频接收方");
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
        svr_remote = findViewById(R.id.svr_remote);
        mEglBase = EglBase.create(); // 创建EglBase实例
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
        // 创建音视频的媒体流
        mMediaStream = mConnFactory.createLocalMediaStream("local_stream");
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
                // 接受方要创建应答
                mPeer.getConnection().createAnswer(mPeer, new MediaConstraints());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        // 第四个参数表示对方接受视频通话之后，如何显示对方的视频画面
        mPeer = new Peer(mSocket, mContact.from, mContact.to, (userId, remoteStream) -> {
            String desc = String.format("from=%s, to=%s", mContact.from, mContact.to);
            Log.d(TAG, "addRemoteStream "+desc);
            ProxyVideoSink remoteSink = new ProxyVideoSink();
            remoteSink.setTarget(svr_remote); // 设置视频轨道中对方的渲染图层
            VideoTrack videoTrack = remoteStream.videoTracks.get(0);
            videoTrack.addSink(remoteSink);
        });
        mPeer.init(mConnFactory, mMediaStream, mIceServers); // 初始化点对点连接
        mSocket.on("other_hang_up", (args) -> dialOff()); // 等待对方挂断通话
        Log.d(TAG, "self_dial_in");
        SocketUtil.emit(mSocket, "self_dial_in", mContact); // 我方同意了视频通话
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("other_dial_in"); // 取消监听对方的接入请求
        mSocket.off("other_hang_up"); // 取消监听对方的挂断请求
        mSocket.off("IceInfo"); // 取消监听流媒体传输
        mSocket.off("SdpInfo"); // 取消监听会话连接
        svr_remote.release(); // 释放远程的渲染器资源（对方）
        if (mSocket.connected()) { // 已经连上Socket服务器
            mSocket.disconnect(); // 断开Socket连接
        }
    }

}