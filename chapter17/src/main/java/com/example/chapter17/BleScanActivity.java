package com.example.chapter17;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chapter17.adapter.BlueListAdapter;
import com.example.chapter17.bean.BlueDevice;
import com.example.chapter17.util.BluetoothUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class BleScanActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "BleScanActivity";
    private CheckBox ck_bluetooth; // 声明一个复选框对象
    private TextView tv_discovery; // 声明一个文本视图对象
    private ListView lv_bluetooth; // 声明一个用于展示蓝牙设备的列表视图对象
    private BlueListAdapter mListAdapter; // 声明一个蓝牙设备的列表适配器对象

    private Map<String, BlueDevice> mDeviceMap = new HashMap<>(); // 蓝牙设备映射
    private List<BlueDevice> mDeviceList = new ArrayList<>(); // 蓝牙设备列表
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private BluetoothAdapter mBluetoothAdapter; // 声明一个蓝牙适配器对象
    private BluetoothDevice mRemoteDevice; // 声明一个蓝牙设备对象
    private BluetoothGatt mBluetoothGatt; // 声明一个蓝牙GATT客户端对象
    private boolean isScaning = false; // 是否正在扫描

    private UUID read_UUID_chara; // 读的特征编号
    private UUID read_UUID_service; // 读的服务编号
    private UUID write_UUID_chara; // 写的特征编号
    private UUID write_UUID_service; // 写的服务编号
    private UUID notify_UUID_chara; // 通知的特征编号
    private UUID notify_UUID_service; // 通知的服务编号
    private UUID indicate_UUID_chara; // 指示的特征编号
    private UUID indicate_UUID_service; // 指示的服务编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_scan);
        initView(); // 初始化视图
        initBluetooth(); // 初始化蓝牙适配器
        if (BluetoothUtil.getBlueToothStatus()) { // 已经打开蓝牙
            ck_bluetooth.setChecked(true);
        }
    }

    // 初始化视图
    private void initView() {
        ck_bluetooth = findViewById(R.id.ck_bluetooth);
        tv_discovery = findViewById(R.id.tv_discovery);
        lv_bluetooth = findViewById(R.id.lv_bluetooth);
        ck_bluetooth.setOnCheckedChangeListener(this);
        mListAdapter = new BlueListAdapter(this, mDeviceList);
        lv_bluetooth.setAdapter(mListAdapter);
        lv_bluetooth.setOnItemClickListener((parent, view, position, id) -> {
            BlueDevice item = mDeviceList.get(position);
            // 根据设备地址获得远端的蓝牙设备对象
            mRemoteDevice = mBluetoothAdapter.getRemoteDevice(item.address);
            Log.d(TAG, "onItemClick address="+mRemoteDevice.getAddress()+", name="+mRemoteDevice.getName());
            // 连接GATT服务器
            mBluetoothGatt = mRemoteDevice.connectGatt(this, false, mGattCallback);
        });
    }

    // 初始化蓝牙适配器
    private void initBluetooth() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "当前设备不支持低功耗蓝牙", Toast.LENGTH_SHORT).show();
            finish(); // 关闭当前页面
        }
        // 获取蓝牙管理器，并从中得到蓝牙适配器
        BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bm.getAdapter(); // 获取蓝牙适配器
    }

    // 创建一个开启BLE扫描的任务
    private Runnable mScanStart = new Runnable() {
        @Override
        public void run() {
            if (!isScaning && BluetoothUtil.getBlueToothStatus()) {
                isScaning = true;
                // 获取BLE设备扫描器
                BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
                scanner.startScan(mScanCallback); // 开始扫描BLE设备
                tv_discovery.setText("正在搜索低功耗蓝牙设备");
            } else {
                mHandler.postDelayed(this, 2000);
            }
        }
    };

    // 创建一个扫描回调对象
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (TextUtils.isEmpty(result.getDevice().getName())) {
                return;
            }
            Log.d(TAG, "callbackType="+callbackType+", result="+result.toString());
            // 下面把找到的蓝牙设备添加到设备映射和设备列表
            BlueDevice device = new BlueDevice(result.getDevice().getName(), result.getDevice().getAddress(), 0);
            mDeviceMap.put(device.address, device);
            mDeviceList.clear();
            mDeviceList.addAll(mDeviceMap.values());
            runOnUiThread(() -> mListAdapter.notifyDataSetChanged());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    // 创建一个停止BLE扫描的任务
    private Runnable mScanStop = () -> {
        isScaning = false;
        // 获取BLE设备扫描器
        BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        scanner.stopScan(mScanCallback); // 停止扫描BLE设备
        tv_discovery.setText("停止搜索低功耗蓝牙设备");
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_bluetooth) {
            if (isChecked) { // 开启蓝牙功能
                ck_bluetooth.setText("蓝牙开");
                if (!BluetoothUtil.getBlueToothStatus()) { // 还未打开蓝牙
                    BluetoothUtil.setBlueToothStatus(true); // 开启蓝牙功能
                }
                mHandler.post(mScanStart); // 启动开始BLE扫描的任务
            } else { // 关闭蓝牙功能
                ck_bluetooth.setText("蓝牙关");
                mHandler.removeCallbacks(mScanStart); // 移除开始BLE扫描的任务
                BluetoothUtil.setBlueToothStatus(false); // 关闭蓝牙功能
                mDeviceList.clear();
                mListAdapter.notifyDataSetChanged();
            }
        }
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
                mHandler.post(mScanStop);
                runOnUiThread(() -> {
                    // 下面把找到的蓝牙设备添加到设备映射和设备列表
                    BlueDevice device = mDeviceMap.get(mRemoteDevice.getAddress());
                    device.state = 1;
                    mDeviceMap.put(device.address, device);
                    mDeviceList.clear();
                    mDeviceList.addAll(mDeviceMap.values());
                    tv_discovery.setText("已连接"+mRemoteDevice.getName());
                    mListAdapter.notifyDataSetChanged();
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
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            read_UUID_chara = chara.getUuid();
                            read_UUID_service = gattService.getUuid();
                            Log.d(TAG, "read_chara=" + read_UUID_chara + ", read_service=" + read_UUID_service);
                        }
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                            write_UUID_chara = chara.getUuid();
                            write_UUID_service = gattService.getUuid();
                            Log.d(TAG, "write_chara=" + write_UUID_chara + ", write_service=" + write_UUID_service);
                        }
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
                            write_UUID_chara = chara.getUuid();
                            write_UUID_service = gattService.getUuid();
                            Log.d(TAG, "no_response write_chara=" + write_UUID_chara + ", write_service=" + write_UUID_service);
                        }
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            notify_UUID_chara = chara.getUuid();
                            notify_UUID_service = gattService.getUuid();
                            Log.d(TAG, "notify_chara=" + notify_UUID_chara + ", notify_service=" + notify_UUID_service);
                        }
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
                            indicate_UUID_chara = chara.getUuid();
                            indicate_UUID_service = gattService.getUuid();
                            Log.d(TAG, "indicate_chara=" + indicate_UUID_chara + ", indicate_service=" + indicate_UUID_service);
                        }
                    }
                }
            } else {
                Log.d(TAG, "onServicesDiscovered fail-->" + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic chara, int status) {
            super.onCharacteristicRead(gatt, chara, status);
            Log.d(TAG, "onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {}
        }

        // 收到BLE服务端的数据变更时回调
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic chara) {
            super.onCharacteristicChanged(gatt, chara);
            String message = new String(chara.getValue()); // 把服务端返回的数据转成字符串
            Log.d(TAG, "onCharacteristicChanged "+message);
        }

        // 收到BLE服务端的数据写入时回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic chara, int status) {
            super.onCharacteristicWrite(gatt, chara, status);
            Log.d(TAG, "onCharacteristicWrite");
            if (status == BluetoothGatt.GATT_SUCCESS) {}
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorWrite");
            if (status == BluetoothGatt.GATT_SUCCESS) {}
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.d(TAG, "onReadRemoteRssi");
            if (status == BluetoothGatt.GATT_SUCCESS) {}
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.d(TAG, "onDescriptorRead");
            if ((status == BluetoothGatt.GATT_SUCCESS)) {}
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mScanStart); // 移除开始BLE扫描的任务
        mHandler.removeCallbacks(mScanStop); // 移除停止BLE扫描的任务
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect(); // 断开GATT连接
        }
    }
}