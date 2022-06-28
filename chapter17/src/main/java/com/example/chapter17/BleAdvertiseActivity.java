package com.example.chapter17;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chapter17.constant.BleConstant;
import com.example.chapter17.util.BluetoothUtil;

@SuppressLint("MissingPermission")
public class BleAdvertiseActivity extends AppCompatActivity {
    private static final String TAG = "BleAdvertiseActivity";
    private CheckBox ck_bluetooth; // 声明一个复选框对象
    private EditText et_name; // 声明一个编辑框对象
    private Button btn_advertise; // 声明一个按钮对象
    private TextView tv_hint; // 声明一个文本视图对象

    private BluetoothManager mBluetoothManager; // 声明一个蓝牙管理器对象
    private BluetoothAdapter mBluetoothAdapter; // 声明一个蓝牙适配器对象
    private BluetoothGattServer mGattServer; // 声明一个蓝牙GATT服务器对象
    private boolean isAdvertising = false; // 是否正在广播

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_advertise);
        initView(); // 初始化视图
        initBluetooth(); // 初始化蓝牙适配器
        if (BluetoothUtil.getBlueToothStatus()) { // 已经打开蓝牙
            ck_bluetooth.setChecked(true);
        }
    }

    // 初始化视图
    private void initView() {
        ck_bluetooth = findViewById(R.id.ck_bluetooth);
        et_name = findViewById(R.id.et_name);
        btn_advertise = findViewById(R.id.btn_advertise);
        tv_hint = findViewById(R.id.tv_hint);
        ck_bluetooth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) { // 开启蓝牙功能
                ck_bluetooth.setText("蓝牙开");
                btn_advertise.setEnabled(true);
                btn_advertise.setText("发送低功耗蓝牙广播");
                if (!BluetoothUtil.getBlueToothStatus()) { // 还未打开蓝牙
                    BluetoothUtil.setBlueToothStatus(true); // 开启蓝牙功能
                }
            } else { // 关闭蓝牙功能
                ck_bluetooth.setText("蓝牙关");
                btn_advertise.setEnabled(false);
                isAdvertising = false;
                BluetoothUtil.setBlueToothStatus(false); // 关闭蓝牙功能
            }
        });
        btn_advertise.setEnabled(false);
        btn_advertise.setOnClickListener(v -> {
            if (!BluetoothUtil.getBlueToothStatus()) { // 还未打开蓝牙
                Toast.makeText(this, "请先开启蓝牙再发送低功耗蓝牙广播", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(et_name.getText().toString())) {
                Toast.makeText(this, "请先输入服务器名称", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isAdvertising) {
                stopAdvertise(); // 停止低功耗蓝牙广播
            } else {
                startAdvertise(et_name.getText().toString()); // 开始低功耗蓝牙广播
            }
            isAdvertising = !isAdvertising;
            btn_advertise.setText(isAdvertising ? "停止低功耗蓝牙广播" : "发送低功耗蓝牙广播");
        });
    }

    // 初始化蓝牙适配器
    private void initBluetooth() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "当前设备不支持低功耗蓝牙", Toast.LENGTH_SHORT).show();
            finish(); // 关闭当前页面
        }
        // 获取蓝牙管理器，并从中得到蓝牙适配器
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter(); // 获取蓝牙适配器
    }

    // 开始低功耗蓝牙广播
    private void startAdvertise(String ble_name) {
        // 设置广播参数
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setConnectable(true) // 是否允许连接
                .setTimeout(0) // 设置超时时间
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .build();
        // 设置广播内容
        AdvertiseData advertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName(true) // 是否把设备名称也广播出去
                .setIncludeTxPowerLevel(true) // 是否把功率电平也广播出去
                .build();
        mBluetoothAdapter.setName(ble_name); // 设置BLE服务端的名称
        // 获取BLE广播器
        BluetoothLeAdvertiser advertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        // BLE服务端开始广播，好让别人发现自己
        advertiser.startAdvertising(settings, advertiseData, mAdvertiseCallback);
    }

    // 停止低功耗蓝牙广播
    private void stopAdvertise() {
        if (mBluetoothAdapter != null) {
            // 获取BLE广播器
            BluetoothLeAdvertiser advertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
            if (advertiser != null) {
                advertiser.stopAdvertising(mAdvertiseCallback); // 停止低功耗蓝牙广播
            }
        }
    }

    // 创建一个低功耗蓝牙广播回调对象
    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settings) {
            Log.d(TAG, "低功耗蓝牙广播成功："+settings.toString());
            addService(); // 添加读写服务UUID，特征值等
            String desc = String.format("BLE服务端“%s”正在对外广播", et_name.getText().toString());
            tv_hint.setText(desc);
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.d(TAG, "低功耗蓝牙广播失败，错误代码为"+errorCode);
            tv_hint.setText("低功耗蓝牙广播失败，错误代码为"+errorCode);
        }
    };

    // 添加读写服务UUID，特征值等
    private void addService() {
        BluetoothGattService gattService = new BluetoothGattService(
                BleConstant.UUID_SERVER, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        // 只读的特征值
        BluetoothGattCharacteristic charaRead = new BluetoothGattCharacteristic(BleConstant.UUID_CHAR_READ,
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);
        // 只写的特征值
        BluetoothGattCharacteristic charaWrite = new BluetoothGattCharacteristic(BleConstant.UUID_CHAR_WRITE,
                BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_WRITE);
        gattService.addCharacteristic(charaRead); // 将特征值添加到服务里面
        gattService.addCharacteristic(charaWrite); // 将特征值添加到服务里面
        // 开启GATT服务器等待客户端连接
        mGattServer = mBluetoothManager.openGattServer(this, mGattCallback);
        mGattServer.addService(gattService); // 向GATT服务器添加指定服务
    }

    // 创建一个GATT服务器回调对象
    private BluetoothGattServerCallback mGattCallback = new BluetoothGattServerCallback() {
        // BLE连接的状态发生变化时回调
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            Log.d(TAG, "onConnectionStateChange device=" + device.toString() + " status=" + status + " newState=" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                runOnUiThread(() -> {
                    String desc = String.format("%s\n已连接BLE客户端，对方名称为%s，MAC地址为%s",
                            tv_hint.getText().toString(), device.getName(), device.getAddress());
                    tv_hint.setText(desc);
                });
            }
        }

        // 收到BLE客户端写入请求时回调
        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic chara, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, chara, preparedWrite, responseNeeded, offset, value);
            String message = new String(value); // 把客户端发来的数据转成字符串
            Log.d(TAG, "收到了客户端发过来的数据 " + message);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAdvertise(); // 停止低功耗蓝牙广播
        if (mGattServer != null) {
            mGattServer.close(); // 关闭GATT服务器
        }
    }

}