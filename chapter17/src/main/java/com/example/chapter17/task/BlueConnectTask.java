package com.example.chapter17.task;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.chapter17.util.BluetoothConnector;

public class BlueConnectTask extends Thread {
    private static final String TAG = "BlueConnectTask";
    private Activity mAct; // 声明一个活动实例
    private BlueConnectListener mListener; // 声明一个蓝牙连接的监听器对象
    private BluetoothDevice mDevice; // 声明一个蓝牙设备对象

    public BlueConnectTask(Activity act, BluetoothDevice device, BlueConnectListener listener) {
        mAct = act;
        mListener = listener;
        mDevice = device;
    }

    @Override
    public void run() {
        // 创建一个对方设备的蓝牙连接器，第一个输入参数为对方的蓝牙设备对象
        BluetoothConnector connector = new BluetoothConnector(mDevice, true,
                BluetoothAdapter.getDefaultAdapter(), null);
        Log.d(TAG, "run");
        // 蓝牙连接需要完整的权限,有些机型弹窗提示"***想进行通信",这就不行,日志会报错:
        // read failed, socket might closed or timeout, read ret: -1
        try {
            // 开始连接，并返回对方设备的蓝牙套接字对象BluetoothSocket
            BluetoothSocket socket = connector.connect().getUnderlyingSocket();
            mAct.runOnUiThread(() -> mListener.onBlueConnect(socket));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 定义一个蓝牙连接的监听器接口，用于在成功连接之后调用onBlueConnect方法
    public interface BlueConnectListener {
        void onBlueConnect(BluetoothSocket socket);
    }

}
