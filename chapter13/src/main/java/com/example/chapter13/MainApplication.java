package com.example.chapter13;

import android.app.Application;

import com.example.chapter13.constant.NetConst;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MainApplication extends Application {
    private static MainApplication mApp; // 声明一个当前应用的静态实例
    public String wechatName; // 自己的微信昵称
    private Socket mSocket; // 声明一个套接字对象

    // 利用单例模式获取当前应用的唯一实例
    public static MainApplication getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this; // 在打开应用时对静态的应用实例赋值
        try {
            String uri = String.format("http://%s:%d/", NetConst.CHAT_IP, NetConst.CHAT_PORT);
            mSocket = IO.socket(uri); // 创建指定地址和端口的套接字实例
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    // 获取套接字对象的唯一实例
    public Socket getSocket() {
        return mSocket;
    }
}
