package com.example.chapter17.task;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.chapter17.util.BluetoothConnector;
import com.example.chapter17.util.BluetoothUtil;

public class BlueAcceptTask extends Thread {
    private static final String TAG = "BlueAcceptTask";
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";
    private static BluetoothServerSocket mServerSocket; // 声明一个蓝牙服务端套接字对象
    private Activity mAct; // 声明一个活动实例
    private BlueAcceptListener mListener; // 声明一个蓝牙侦听的监听器对象

    public BlueAcceptTask(Activity act, boolean secure, BlueAcceptListener listener) {
        mAct = act;
        mListener = listener;
        Log.d(TAG, "init");
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        // 以下提供了三种侦听方法，使得在不同情况下都能获得服务端的Socket对象
        try {
            if (mServerSocket != null) {
                mServerSocket.close();
            }
            if (secure) { // 安全连接
                mServerSocket = adapter.listenUsingRfcommWithServiceRecord(
                        NAME_SECURE, BluetoothConnector.uuid);
            } else { // 不安全连接
                mServerSocket = adapter.listenUsingInsecureRfcommWithServiceRecord(
                        NAME_INSECURE, BluetoothConnector.uuid);
            }
        } catch (Exception e) { // 遇到异常则尝试第三种侦听方式
            e.printStackTrace();
            mServerSocket = BluetoothUtil.listenServer(adapter);
        }
    }

    @Override
    public void run() {
        Log.d(TAG, "run");
        while (true) {
            try {
                // 如果accept方法有返回，则表示某部设备过来打招呼了
                BluetoothSocket socket = mServerSocket.accept();
                if (socket != null) { // socket非空，表示名花有主了，赶紧带去见公婆
                    mAct.runOnUiThread(() -> mListener.onBlueAccept(socket));
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // 定义一个蓝牙侦听的监听器接口，在获得响应之后回调onBlueAccept方法
    public interface BlueAcceptListener {
        void onBlueAccept(BluetoothSocket socket);
    }

}
