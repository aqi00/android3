package com.example.chapter17;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter17.adapter.ScanListAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WifiScanActivity extends AppCompatActivity {
    private final static String TAG = "WifiScanActivity";
    private TextView tv_result; // 声明一个文本视图对象
    private ListView lv_scan; // 声明一个列表视图对象
    private WifiManager mWifiManager; // 声明一个WiFi管理器对象
    private WifiScanReceiver mWifiScanReceiver = new WifiScanReceiver(); // 声明一个WiFi扫描接收器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scan);
        tv_result = findViewById(R.id.tv_result);
        lv_scan = findViewById(R.id.lv_scan);
        findViewById(R.id.btn_scan).setOnClickListener(v -> mWifiManager.startScan());
        // 从系统服务中获取WiFi管理器
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mWifiScanReceiver, filter); // 注册WiFi扫描的广播接收器
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mWifiScanReceiver); // 注销WiFi扫描的广播接收器
    }

    // 定义一个扫描周边WiFi的广播接收器
    private class WifiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 获取WiFi扫描的结果列表
            List<ScanResult> scanList = mWifiManager.getScanResults();
            if (scanList != null) {
                // 查找符合80211标准的WiFi路由器集合
                Map<String, ScanResult> m80211mcMap = find80211mcResults(scanList);
                runOnUiThread(() -> showScanResult(scanList, m80211mcMap));
            }
        }
    }

    // 查找符合80211标准的WiFi路由器集合
    private Map<String, ScanResult> find80211mcResults(List<ScanResult> originList) {
        Map<String, ScanResult> resultMap = new HashMap<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (ScanResult scanResult : originList) { // 遍历扫描发现的WiFi列表
                if (scanResult.is80211mcResponder()) { // 符合80211标准
                    resultMap.put(scanResult.BSSID, scanResult); // BSSID表示MAC地址
                }
            }
        }
        return resultMap;
    }

    // 显示过滤后的WiFi扫描结果
    private void showScanResult(List<ScanResult> list, Map<String, ScanResult> map) {
        tv_result.setText(String.format("找到%d个WiFi热点，其中有%d个支持RTT。",
                                        list.size(), map.size()));
        lv_scan.setAdapter(new ScanListAdapter(this, list, map));
    }

}