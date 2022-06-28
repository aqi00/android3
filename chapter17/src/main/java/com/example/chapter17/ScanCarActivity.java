package com.example.chapter17;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
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

@SuppressLint("MissingPermission")
public class ScanCarActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "ScanCarActivity";
    private CheckBox ck_bluetooth; // 声明一个复选框对象
    private TextView tv_discovery; // 声明一个文本视图对象
    private ListView lv_bluetooth; // 声明一个用于展示蓝牙设备的列表视图对象
    private BlueListAdapter mListAdapter; // 声明一个蓝牙设备的列表适配器对象

    private Map<String, BlueDevice> mDeviceMap = new HashMap<>(); // 蓝牙设备映射
    private List<BlueDevice> mDeviceList = new ArrayList<>(); // 蓝牙设备列表
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private BluetoothAdapter mBluetoothAdapter; // 声明一个蓝牙适配器对象
    private boolean isScaning = false; // 是否正在扫描

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_car);
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
            Intent intent = new Intent(this, SmartCarActivity.class);
            intent.putExtra("address", item.address);
            startActivity(intent);
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
                tv_discovery.setText("点击BLE设备进入管理界面");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mScanStart); // 移除开始BLE扫描的任务
        mHandler.removeCallbacks(mScanStop); // 移除停止BLE扫描的任务
    }

}