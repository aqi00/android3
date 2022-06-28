package com.example.chapter16;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.chapter16.util.DateUtil;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

public class MapLocationActivity extends AppCompatActivity implements TencentLocationListener {
    private final static String TAG = "MapLocationActivity";
    private TencentLocationManager mLocationManager; // 声明一个腾讯定位管理器对象
    private TextView tv_location; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);
        tv_location = findViewById(R.id.tv_location);
        initLocation(); // 初始化定位服务
    }

    // 初始化定位服务
    private void initLocation() {
        mLocationManager = TencentLocationManager.getInstance(this);
        // 创建腾讯定位请求对象
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setInterval(30000).setAllowGPS(true);
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA);
        int error = mLocationManager.requestLocationUpdates(request, this); // 开始定位监听
        if (error == 0) {
            Log.d(TAG, "注册位置监听器成功！");
        } else {
            Log.d(TAG, "注册位置监听器失败！");
        }
    }

    @Override
    public void onLocationChanged(TencentLocation location, int resultCode, String resultDesc) {
        if (resultCode == TencentLocation.ERROR_OK && location != null) { // 定位成功
            String desc = String.format("您当前的位置信息如下：\n定位时间：%s\n" +
                            "纬度：%f\n经度：%f\n省份：%s\n城市：%s\n" +
                            "区域：%s\n街道：%s\n门牌号：%s\n详细地址：%s",
                    DateUtil.formatDate(location.getTime()),
                    location.getLatitude(), location.getLongitude(),
                    location.getProvince(), location.getCity(),
                    location.getDistrict(), location.getStreet(),
                    location.getStreetNo(), location.getAddress());
            tv_location.setText(desc);
        } else { // 定位失败
            Log.d(TAG, "定位失败，错误代码为"+resultCode+"，错误描述为"+resultDesc);
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationManager.removeUpdates(this); // 移除定位监听
    }
}