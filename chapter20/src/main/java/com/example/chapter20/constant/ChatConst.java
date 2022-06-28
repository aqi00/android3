package com.example.chapter20.constant;

import org.webrtc.PeerConnection;

import java.util.ArrayList;
import java.util.List;

public class ChatConst {
    public final static String CHAT_IP = "192.168.1.7"; // 聊天服务的ip
    public final static int CHAT_PORT = 9012; // 聊天服务的端口

    private final static String STUN_URL = "stun:192.168.1.7";
    private final static String STUN_USERNAME = "admin";
    private final static String STUN_PASSWORD = "123456";

    // 获取ICE服务器列表
    public static List<PeerConnection.IceServer> getIceServerList() {
        List<PeerConnection.IceServer> iceServerList = new ArrayList<>();
        iceServerList.add(PeerConnection.IceServer.builder(STUN_URL)
                .setUsername(STUN_USERNAME).setPassword(STUN_PASSWORD).createIceServer());
        return iceServerList;
    }
}
