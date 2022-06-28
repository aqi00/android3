package com.example.chapter20.webrtc;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

import java.util.List;

import io.socket.client.Socket;

public class Peer implements PeerConnection.Observer, SdpObserver {
    private final static String TAG = "Peer";
    private PeerConnection mConn; // 声明一个点对点连接对象
    private Socket mSocket; // 声明一个套接字对象
    private String mSourceId; // 来源设备标识
    private String mDestId; // 目标设备标识
    private PeerStreamListener mStreamListener; // 声明一个点对点流传输监听器对象

    public Peer(Socket socket, String sourceId, String destId, PeerStreamListener streamListener) {
        mSocket = socket;
        mSourceId = sourceId;
        mDestId = destId;
        mStreamListener = streamListener;
    }

    // 初始化点对点连接
    public void init(PeerConnectionFactory factory, MediaStream stream, List<PeerConnection.IceServer> iceServers) {
        Log.d(TAG, "init");
        // 根据ICE服务器列表创建RTC配置对象
        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        mConn = factory.createPeerConnection(rtcConfig, this); // 创建点对点连接
        mConn.addStream(stream); // 给点对点连接添加来源媒体流
    }

    // 获取点对点连接
    public PeerConnection getConnection() {
        return mConn;
    }

    // 以下方法重写自PeerConnection.Observer
    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {}

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        if (iceConnectionState == PeerConnection.IceConnectionState.CONNECTED) {
            Log.d(TAG, "已成功连接!");
        } else if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
            Log.d(TAG, "已断开连接!");
        }
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {}

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {}

    // 收到ICE候选者时回调
    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        try {
            JSONObject json = new JSONObject();
            // 与候选者相关的媒体流识别标签（代表每一路流，比如视频就是0）
            json.put("id", iceCandidate.sdpMid);
            // 在SDP中的索引值 (比如说sdp有两个流，一个视频一个音频，那么视频是第1个，音频是第2个)
            json.put("label", iceCandidate.sdpMLineIndex);
            json.put("candidate", iceCandidate.sdp); // 候选者描述信息
            json.put("source", mSourceId); // 来源标识
            json.put("destination", mDestId); // 目标标识
            mSocket.emit("IceInfo", json); // 往Socket服务器发送JSON数据
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {}

    // 在添加媒体流时回调
    @Override
    public void onAddStream(MediaStream mediaStream) {
        Log.d(TAG, "onAddStream");
        // 给来源方添加目标方的远程媒体流
        mStreamListener.addRemoteStream(mSourceId, mediaStream);
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {}

    @Override
    public void onDataChannel(DataChannel dataChannel) {}

    @Override
    public void onRenegotiationNeeded() {}

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {}

    // 以下方法重写自SdpObserver
    // 在SDP连接创建成功时回调
    @Override
    public void onCreateSuccess(SessionDescription sessionDesc) {
        Log.d(TAG, "onCreateSuccess");
        // 设置本地连接的会话描述
        mConn.setLocalDescription(this, sessionDesc);
        JSONObject json = new JSONObject();
        try {
            json.put("type", sessionDesc.type.canonicalForm()); // 连接类型
            json.put("description", sessionDesc.description); // 连接描述
            json.put("source", mSourceId); // 来源标识
            json.put("destination", mDestId); // 目标标识
            mSocket.emit("SdpInfo", json); // 往Socket服务器发送JSON数据
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSetSuccess() {}

    @Override
    public void onCreateFailure(String s) {}

    @Override
    public void onSetFailure(String s) {}

    // 定义一个点对点流媒体监听器，在收到目标方响应之后，将目标方的媒体流添加至本地
    public interface PeerStreamListener {
        void addRemoteStream(String userId, MediaStream remoteStream);
    }
}
