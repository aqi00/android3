package com.example.chapter17;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.chapter17.adapter.BlueListAdapter;
import com.example.chapter17.bean.BlueDevice;
import com.example.chapter17.task.BlueAcceptTask;
import com.example.chapter17.task.BlueConnectTask;
import com.example.chapter17.task.BlueReceiveTask;
import com.example.chapter17.util.BluetoothUtil;
import com.example.chapter17.widget.InputDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressLint("MissingPermission")
public class BluetoothTransActivity extends AppCompatActivity implements
        OnItemClickListener, OnCheckedChangeListener {
    private static final String TAG = "BluetoothTransActivity";
    private CheckBox ck_bluetooth; // 声明一个复选框对象
    private TextView tv_discovery; // 声明一个文本视图对象
    private ListView lv_bluetooth; // 声明一个用于展示蓝牙设备的列表视图对象
    private BluetoothAdapter mBluetooth; // 声明一个蓝牙适配器对象
    private BlueListAdapter mListAdapter; // 声明一个蓝牙设备的列表适配器对象
    private List<BlueDevice> mDeviceList = new ArrayList<>(); // 蓝牙设备列表
    private int mOpenCode = 1; // 是否允许扫描蓝牙设备的选择对话框返回结果代码
    private Map<String, Integer> mMapState = new HashMap<>(); // 蓝牙状态映射
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private BluetoothSocket mBlueSocket; // 声明一个蓝牙设备的套接字对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_trans);
        initBluetooth(); // 初始化蓝牙适配器
        ck_bluetooth = findViewById(R.id.ck_bluetooth);
        tv_discovery = findViewById(R.id.tv_discovery);
        lv_bluetooth = findViewById(R.id.lv_bluetooth);
        ck_bluetooth.setOnCheckedChangeListener(this);
        if (BluetoothUtil.getBlueToothStatus()) { // 已经打开蓝牙
            ck_bluetooth.setChecked(true);
        }
        initBlueDevice(); // 初始化蓝牙设备列表
    }

    // 初始化蓝牙适配器
    private void initBluetooth() {
        // 获取系统默认的蓝牙适配器
        mBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (mBluetooth == null) {
            Toast.makeText(this, "当前设备未找到蓝牙功能", Toast.LENGTH_SHORT).show();
            finish(); // 关闭当前页面
        }
    }

    // 初始化蓝牙设备列表
    private void initBlueDevice() {
        mDeviceList.clear();
        // 获取已经配对的蓝牙设备集合
        Set<BluetoothDevice> bondedDevices = mBluetooth.getBondedDevices();
        for (BluetoothDevice device : bondedDevices) {
            int state = mMapState.containsKey(device.getAddress()) ?
                    mMapState.get(device.getAddress()) : device.getBondState();
            mDeviceList.add(new BlueDevice(device.getName(), device.getAddress(), state));
        }
        if (mListAdapter == null) { // 首次打开页面，则创建一个新的蓝牙设备列表
            mListAdapter = new BlueListAdapter(this, mDeviceList);
            lv_bluetooth.setAdapter(mListAdapter);
            lv_bluetooth.setOnItemClickListener(this);
        } else { // 不是首次打开页面，则刷新蓝牙设备列表
            mListAdapter.notifyDataSetChanged();
        }
    }

    private Runnable mDiscoverable = new Runnable() {
        public void run() {
            // Android8.0要在已打开蓝牙功能时才会弹出下面的选择窗
            if (BluetoothUtil.getBlueToothStatus()) { // 已经打开蓝牙
                // 弹出是否允许扫描蓝牙设备的选择对话框
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(intent, mOpenCode);
            } else {
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_bluetooth) {
            if (isChecked) { // 开启蓝牙功能
                ck_bluetooth.setText("蓝牙开");
                if (!BluetoothUtil.getBlueToothStatus()) { // 还未打开蓝牙
                    BluetoothUtil.setBlueToothStatus(true); // 开启蓝牙功能
                }
                mHandler.post(mDiscoverable);
                mHandler.postDelayed(mAccept, 1000); // 服务端需要，客户端不需要
            } else { // 关闭蓝牙功能
                ck_bluetooth.setText("蓝牙关");
                cancelDiscovery(); // 取消蓝牙设备的搜索
                BluetoothUtil.setBlueToothStatus(false); // 关闭蓝牙功能
                initBlueDevice(); // 初始化蓝牙设备列表
            }
        }
    }

    // 定义一个连接侦听任务
    private Runnable mAccept = new Runnable() {
        @Override
        public void run() {
            if (mBluetooth.getState() == BluetoothAdapter.STATE_ON) { // 已连接
                // 创建一个蓝牙设备侦听线程，成功侦听就启动蓝牙消息的接收任务
                BlueAcceptTask acceptTask = new BlueAcceptTask(
                        BluetoothTransActivity.this, true, socket -> startReceiveTask(socket));
                acceptTask.start();
            } else { // 未连接
                // 延迟1秒后重新启动连接侦听任务
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == mOpenCode) { // 来自允许蓝牙扫描的对话框
            // 延迟50毫秒后启动蓝牙设备的刷新任务
            mHandler.postDelayed(mRefresh, 50);
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "允许本地蓝牙被附近的其他蓝牙设备发现",
                        Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "不允许蓝牙被附近的其他蓝牙设备发现",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 定义一个刷新任务，每隔两秒刷新扫描到的蓝牙设备
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            beginDiscovery(); // 开始扫描周围的蓝牙设备
            // 延迟30秒后再次启动蓝牙设备的刷新任务
            mHandler.postDelayed(this, 30*1000);
        }
    };

    // 开始扫描周围的蓝牙设备
    private void beginDiscovery() {
        // 如果当前不是正在搜索，则开始新的搜索任务
        if (!mBluetooth.isDiscovering()) {
            initBlueDevice(); // 初始化蓝牙设备列表
            tv_discovery.setText("正在搜索蓝牙设备");
            mBluetooth.startDiscovery(); // 开始扫描周围的蓝牙设备
        }
    }

    // 取消蓝牙设备的搜索
    private void cancelDiscovery() {
        mHandler.removeCallbacks(mRefresh);
        tv_discovery.setText("取消搜索蓝牙设备");
        // 当前正在搜索，则取消搜索任务
        if (mBluetooth.isDiscovering()) {
            mBluetooth.cancelDiscovery(); // 取消扫描周围的蓝牙设备
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.postDelayed(mRefresh, 50);
        // 需要过滤多个动作，则调用IntentFilter对象的addAction添加新动作
        IntentFilter discoveryFilter = new IntentFilter();
        discoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);
        discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        discoveryFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        // 注册蓝牙设备搜索的广播接收器
        registerReceiver(discoveryReceiver, discoveryFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelDiscovery(); // 取消蓝牙设备的搜索
        unregisterReceiver(discoveryReceiver); // 注销蓝牙设备搜索的广播接收器
    }

    // 蓝牙设备的搜索结果通过广播返回
    private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive action=" + action);
            // 获得已经搜索到的蓝牙设备
            if (action.equals(BluetoothDevice.ACTION_FOUND)) { // 发现新的蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d(TAG, "name=" + device.getName() + ", state=" + device.getBondState());
                refreshDevice(device, device.getBondState()); // 将发现的蓝牙设备加入到设备列表
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) { // 搜索完毕
                //mHandler.removeCallbacks(mRefresh); // 需要持续搜索就要注释这行
                tv_discovery.setText("蓝牙设备搜索完成");
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) { // 配对状态变更
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    tv_discovery.setText("正在配对" + device.getName());
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    tv_discovery.setText("完成配对" + device.getName());
                    mHandler.postDelayed(mRefresh, 50);
                } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    tv_discovery.setText("取消配对" + device.getName());
                    refreshDevice(device, device.getBondState()); // 刷新蓝牙设备列表
                }
            }
        }
    };

    // 刷新蓝牙设备列表
    private void refreshDevice(BluetoothDevice device, int state) {
        int i;
        for (i = 0; i < mDeviceList.size(); i++) {
            BlueDevice item = mDeviceList.get(i);
            if (item.address.equals(device.getAddress())) {
                if (item.state != BlueListAdapter.CONNECTED) {
                    item.state = state;
                    mDeviceList.set(i, item);
                    mMapState.put(item.address, state);
                }
                break;
            }
        }
        if (i >= mDeviceList.size()) {
            mDeviceList.add(new BlueDevice(device.getName(), device.getAddress(), state));
        }
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //cancelDiscovery();
        BlueDevice item = mDeviceList.get(position);
        // 根据设备地址获得远端的蓝牙设备对象
        BluetoothDevice device = mBluetooth.getRemoteDevice(item.address);
        Log.d(TAG, "getBondState="+device.getBondState()+", item.state="+item.state);
        if (device.getBondState() == BluetoothDevice.BOND_NONE) { // 尚未配对
            BluetoothUtil.createBond(device); // 创建配对信息
        } else if (device.getBondState() == BluetoothDevice.BOND_BONDED &&
                item.state != BlueListAdapter.CONNECTED) { // 已经配对但尚未连接
            tv_discovery.setText("开始连接");
            // 创建一个蓝牙设备连接线程，成功连接就启动蓝牙消息的接收任务
            BlueConnectTask connectTask = new BlueConnectTask(this, device, socket -> startReceiveTask(socket));
            connectTask.start();
        } else if (device.getBondState() == BluetoothDevice.BOND_BONDED &&
                item.state == BlueListAdapter.CONNECTED) { // 已经配对且已经连接
            tv_discovery.setText("正在发送消息");
            // 弹出消息输入对话框。准备向对方发送消息，往蓝牙设备套接字中写入消息数据
            InputDialog dialog = new InputDialog(this, "", 0, "请输入要发送的消息",
                    (idt, content, seq) -> BluetoothUtil.writeOutputStream(mBlueSocket, content));
            dialog.show();
        }
    }

    // 启动蓝牙消息的接收任务
    private void startReceiveTask(BluetoothSocket socket) {
        if (socket == null) {
            return;
        }
        tv_discovery.setText("连接成功");
        mBlueSocket = socket;
        refreshDevice(mBlueSocket.getRemoteDevice(), BlueListAdapter.CONNECTED);
        // 创建一个蓝牙消息的接收线程
        BlueReceiveTask receiveTask = new BlueReceiveTask(this, mBlueSocket, message -> {
            if (!TextUtils.isEmpty(message)) {
                // 弹出收到消息的提醒对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("我收到消息啦").setMessage(message);
                builder.setPositiveButton("确定", null);
                builder.create().show();
            }
        });
        receiveTask.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBlueSocket != null) {
            try {
                mBlueSocket.close(); // 关闭蓝牙设备的套接字
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
