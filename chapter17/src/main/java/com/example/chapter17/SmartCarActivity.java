package com.example.chapter17;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class SmartCarActivity extends AppCompatActivity {
    private static final String TAG = "SmartCarActivity";
    private TextView tv_name; // 声明一个文本视图对象
    private TextView tv_address; // 声明一个文本视图对象
    private TextView tv_status; // 声明一个文本视图对象
    private Button btn_connect; // 声明一个按钮对象
    private LinearLayout ll_control; // 声明一个线性视图对象
    private SeekBar sb_left; // 声明一个拖动条对象
    private TextView tv_left; // 声明一个文本视图对象
    private SeekBar sb_right; // 声明一个拖动条对象
    private TextView tv_right; // 声明一个文本视图对象

    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private BluetoothAdapter mBluetoothAdapter; // 声明一个蓝牙适配器对象
    private BluetoothDevice mRemoteDevice; // 声明一个蓝牙设备对象
    private BluetoothGatt mBluetoothGatt; // 声明一个蓝牙GATT客户端对象

    private UUID write_UUID_chara; // 写的特征编号
    private UUID write_UUID_service; // 写的服务编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_car);
        initView(); // 初始化视图
        initBluetooth(); // 初始化蓝牙适配器
    }

    // 初始化视图
    private void initView() {
        tv_name = findViewById(R.id.tv_name);
        tv_address = findViewById(R.id.tv_address);
        tv_status = findViewById(R.id.tv_status);
        btn_connect = findViewById(R.id.btn_connect);
        ll_control = findViewById(R.id.ll_control);
        sb_left = findViewById(R.id.sb_left);
        tv_left = findViewById(R.id.tv_left);
        sb_right = findViewById(R.id.sb_right);
        tv_right = findViewById(R.id.tv_right);
        btn_connect.setOnClickListener(v -> {
            // 连接GATT服务器
            mBluetoothGatt = mRemoteDevice.connectGatt(this, false, mGattCallback);
        });
        findViewById(R.id.btn_whistle).setOnClickListener(v -> sendCommand(0x08));
        sb_left.setOnSeekBarChangeListener(mLeftListener);
        sb_right.setOnSeekBarChangeListener(mRightListener);
        findViewById(R.id.btn_go_forward).setOnClickListener(v -> sendCommand(0x02));
        findViewById(R.id.btn_go_back).setOnClickListener(v -> sendCommand(0x03));
        findViewById(R.id.btn_turn_left).setOnClickListener(v -> sendCommand(0x04));
        findViewById(R.id.btn_turn_right).setOnClickListener(v -> sendCommand(0x05));
    }

    // 创建一个调整左电机功率的拖动监听器
    private SeekBar.OnSeekBarChangeListener mLeftListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress()==0 ? 1 : seekBar.getProgress();
            sendCommand(50 + progress); // 发送指令调整左电机功率
            tv_left.setText(""+progress);
        }
    };

    // 创建一个调整右电机功率的拖动监听器
    private SeekBar.OnSeekBarChangeListener mRightListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress()==0 ? 1 : seekBar.getProgress();
            sendCommand(150 + progress); // 发送指令调整右电机功率
            tv_right.setText(""+progress);
        }
    };

    private boolean isRunning = false; // 小车是否正在工作
    // 向智能小车发送指令
    private void sendCommand(int command) {
        if (isRunning) {
            return;
        }
        if (command>=0x02 && command<=0x05) { // 上下左右运动
            isRunning = true;
            new Thread(() -> writeCommand((byte) command)).start();
            // 延迟200毫秒后停止运动
            mHandler.postDelayed(() -> {
                isRunning = false;
                sendCommand(0x01); // 发送停止运动的指令
            }, 200);
        } else if (command == 0x08) { // 鸣笛
            isRunning = true;
            new Thread(() -> writeCommand((byte) command)).start();
            // 延迟200毫秒后停止鸣笛
            mHandler.postDelayed(() -> {
                isRunning = false;
                sendCommand(0x09); // 发送停止鸣笛的指令
            }, 200);
        } else { // 调整电机功率、停止鸣笛、停止运动
            new Thread(() -> writeCommand((byte) command)).start();
        }
    }

    // 往GATT服务端的写特征值写入指令
    private void writeCommand(byte command) {
        // 拿到写的特征值
        BluetoothGattCharacteristic chara = mBluetoothGatt.getService(write_UUID_service)
                .getCharacteristic(write_UUID_chara);
        Log.d(TAG, "writeCharacteristic "+command);
        chara.setValue(new byte[]{command}); // 设置写特征值
        mBluetoothGatt.writeCharacteristic(chara); // 往GATT服务器写入特征值
    }

    // 初始化蓝牙适配器
    private void initBluetooth() {
        // 获取蓝牙管理器，并从中得到蓝牙适配器
        BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bm.getAdapter(); // 获取蓝牙适配器
        String address = getIntent().getStringExtra("address");
        // 根据设备地址获得远端的蓝牙设备对象
        mRemoteDevice = mBluetoothAdapter.getRemoteDevice(address);
        tv_name.setText("设备名称："+mRemoteDevice.getName());
        tv_address.setText("设备MAC："+mRemoteDevice.getAddress());
        tv_status.setText("连接状态：未连接");
    }

    // 创建一个GATT客户端回调对象
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        // BLE连接的状态发生变化时回调
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.d(TAG, "onConnectionStateChange status="+status+", newState="+newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) { // 连接成功
                gatt.discoverServices(); // 开始查找GATT服务器提供的服务
                runOnUiThread(() -> {
                    tv_status.setText("已连接");
                    btn_connect.setVisibility(View.GONE);
                    ll_control.setVisibility(View.VISIBLE);
                });
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) { // 连接断开
                mBluetoothGatt.close(); // 关闭GATT客户端
            }
        }

        // 发现BLE服务端的服务列表及其特征值时回调
        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.d(TAG, "onServicesDiscovered status"+status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // 获取GATT服务器提供的服务列表
                List<BluetoothGattService> gattServiceList= mBluetoothGatt.getServices();
                for (BluetoothGattService gattService : gattServiceList) {
                    List<BluetoothGattCharacteristic> charaList = gattService.getCharacteristics();
                    for (BluetoothGattCharacteristic chara : charaList) {
                        int charaProp = chara.getProperties(); // 获取该特征的属性
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                            write_UUID_chara = chara.getUuid();
                            write_UUID_service = gattService.getUuid();
                            Log.d(TAG, "no_response write_chara=" + write_UUID_chara + ", write_service=" + write_UUID_service);
                        }
                    }
                }
            } else {
                Log.d(TAG, "onServicesDiscovered fail-->" + status);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect(); // 断开GATT连接
        }
    }

}