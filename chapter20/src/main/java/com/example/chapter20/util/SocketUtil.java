package com.example.chapter20.util;

import android.app.Activity;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import io.socket.client.Socket;

public class SocketUtil {

    // 把对象数据转换为json串，然后发给Socket服务器
    public static void emit(Socket socket, String event, Object obj) {
        try {
            JSONObject json = new JSONObject(new Gson().toJson(obj));
            socket.emit(event, json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 检查能否连上Socket服务器
    public static void checkSocketAvailable(Activity act, String host, int port) {
        new Thread(() -> {
            try (java.net.Socket socket = new java.net.Socket()) {
                SocketAddress address = new InetSocketAddress(host, port);
                socket.connect(address, 1500);
            } catch (Exception e) {
                e.printStackTrace();
                act.runOnUiThread(() -> {
                    Toast.makeText(act, "无法连接Socket服务器", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

}
