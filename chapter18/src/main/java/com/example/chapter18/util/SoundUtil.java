package com.example.chapter18.util;

import android.util.Log;

import com.example.chapter18.constant.SoundConstant;

import java.net.URI;
import java.security.MessageDigest;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

public class SoundUtil {
    private final static String TAG = "SoundUtil";

    // 启动语音处理任务（语音识别或者语音合成）
    public static void startSoundTask(String url, Object task) {
        long time = System.currentTimeMillis();
        StringBuilder paramBuilder = new StringBuilder();
        // 填写该应用在开放平台上申请的密钥和密码
        paramBuilder.append(SoundConstant.APP_KEY).append(time).
                append(SoundConstant.APP_SECRET);
        String sign = getSHA256Digest(paramBuilder.toString());

        StringBuilder param = new StringBuilder();
        param.append("appkey=").append(SoundConstant.APP_KEY).append("&")
                .append("time=").append(time).append("&")
                .append("sign=").append(sign);
        String fullUrl = url + param.toString();
        Log.d(TAG, "fullUrl="+fullUrl);
        // 获取WebSocket容器
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            URI uri = new URI(fullUrl); // 创建一个URI对象
            // 连接WebSocket服务器，并关联语音处理任务获得连接会话
            Session session = container.connectToServer(task, uri);
            // 设置文本消息的最大缓存大小
            session.setMaxTextMessageBufferSize(1024 * 1024 * 10);
            // 设置二进制消息的最大缓存大小
            session.setMaxBinaryMessageBufferSize(1024 * 1024 * 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获得SHA摘要
    private static String getSHA256Digest(String data) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(data.getBytes("UTF-8"));
            digest = byte2hex(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return digest;
    }

    // 二进制转十六进制字符串
    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

}
