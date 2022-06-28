package com.example.chapter13.task;

import android.app.Activity;
import android.util.Log;

import javax.websocket.*;

@ClientEndpoint
public class AppClientEndpoint {
    private final static String TAG = "AppClientEndpoint";
    private Activity mAct; // 声明一个活动实例
    private OnRespListener mListener; // 消息应答监听器
    private Session mSession; // 连接会话

    public AppClientEndpoint(Activity act, OnRespListener listener) {
        mAct = act;
        mListener = listener;
    }

    // 向服务器发送请求报文
    public void sendRequest(String req) {
        Log.d(TAG, "发送请求报文："+req);
        try {
            if (mSession != null) {
                RemoteEndpoint.Basic remote = mSession.getBasicRemote();
                remote.sendText(req); // 发送文本数据
                // remote.sendBinary(buffer); // 发送二进制数据
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 连接成功后调用
    @OnOpen
    public void onOpen(final Session session) {
        mSession = session;
        Log.d(TAG, "成功创建连接");
    }

    // 收到服务端消息时调用
    @OnMessage
    public void processMessage(Session session, String message) {
        Log.d(TAG, "WebSocket服务端返回：" + message);
        if (mListener != null) {
            mAct.runOnUiThread(() -> mListener.receiveResponse(message));
        }
    }

    // 收到服务端错误时调用
    @OnError
    public void processError(Throwable t) {
        t.printStackTrace();
    }

    // 定义一个WebSocket应答的监听器接口
    public interface OnRespListener {
        void receiveResponse(String resp);
    }
}
