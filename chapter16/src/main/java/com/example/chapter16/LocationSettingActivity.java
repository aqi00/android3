package com.example.chapter16;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter16.util.SwitchUtil;

@SuppressLint("SetTextI18n")
public class LocationSettingActivity extends AppCompatActivity {
    private CheckBox ck_gps; // 声明一个定位功能的复选框对象
    private CheckBox ck_wlan; // 声明一个WLAN功能的复选框对象
    private CheckBox ck_mobiledata; // 声明一个数据连接功能的复选框对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_setting);
        ck_gps = findViewById(R.id.ck_gps);
        ck_wlan = findViewById(R.id.ck_wlan);
        ck_mobiledata = findViewById(R.id.ck_mobiledata);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 获取定位功能的开关状态
        boolean isGpsOpen = SwitchUtil.getLocationStatus(this);
        ck_gps.setChecked(isGpsOpen);
        ck_gps.setText("定位功能" + ((isGpsOpen)?"开启":"关闭"));
        // 获取WLAN功能的开关状态
        boolean isWlanOpen = SwitchUtil.getWlanStatus(this);
        ck_wlan.setChecked(isWlanOpen);
        ck_wlan.setText("WLAN功能" + ((isWlanOpen)?"开启":"关闭"));
        // 获取数据连接功能的开关状态
        boolean isMobileOpen = SwitchUtil.getMobileDataStatus(this);
        ck_mobiledata.setChecked(isMobileOpen);
        ck_mobiledata.setText("数据连接" + ((isMobileOpen)?"开启":"关闭"));
        ck_gps.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 跳转到系统的定位设置页面
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        });
        ck_wlan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // 跳转到系统的WLAN设置页面
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            } else { // Android10之后，普通应用不能直接开关WLAN
                // 设置WLAN功能的开关状态
                SwitchUtil.setWlanStatus(this, isChecked);
                ck_wlan.setText("WLAN功能" + ((isChecked)?"开启":"关闭"));
            }
        });
        ck_mobiledata.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 跳转到系统的移动网络设置页面
            startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS));
        });
    }

}
