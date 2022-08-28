package com.example.chapter16;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chapter16.task.GetAddressTask;
import com.example.chapter16.util.DateUtil;
import com.example.chapter16.util.SwitchUtil;

import java.util.HashMap;
import java.util.Map;

@SuppressLint(value={"DefaultLocale","SetTextI18n"})
public class LocationAddressActivity extends AppCompatActivity {
    private final static String TAG = "LocationAddressActivity";
    private Map<String,String> providerMap = new HashMap<>();
    private TextView tv_location; // 声明一个文本视图对象
    private String mLocationDesc = ""; // 定位说明
    private LocationManager mLocationMgr; // 声明一个定位管理器对象
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private boolean isLocationEnable = false; // 定位服务是否可用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_address);
        providerMap.put("gps", "卫星定位");
        providerMap.put("network", "网络定位");
        tv_location = findViewById(R.id.tv_location);
        SwitchUtil.checkLocationIsOpen(this, "需要打开定位功能才能查看定位信息");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.removeCallbacks(mRefresh); // 移除定位刷新任务
        initLocation(); // 初始化定位服务
        mHandler.postDelayed(mRefresh, 100); // 延迟100毫秒启动定位刷新任务
    }

    // 初始化定位服务
    private void initLocation() {
        // 从系统服务中获取定位管理器
        mLocationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria(); // 创建一个定位准则对象
        // 设置定位精确度。Criteria.ACCURACY_COARSE表示粗略，Criteria.ACCURACY_FIN表示精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true); // 设置是否需要海拔信息
        criteria.setBearingRequired(true); // 设置是否需要方位信息
        criteria.setCostAllowed(true); // 设置是否允许运营商收费
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 设置对电源的需求
        // 获取定位管理器的最佳定位提供者
        String bestProvider = mLocationMgr.getBestProvider(criteria, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 实测发现部分手机的android11系统使用卫星定位会没返回
            bestProvider = "network";
        }
        if (mLocationMgr.isProviderEnabled(bestProvider)) { // 定位提供者当前可用
            tv_location.setText("正在获取" + providerMap.get(bestProvider) + "对象");
            mLocationDesc = String.format("定位类型为%s", providerMap.get(bestProvider));
            beginLocation(bestProvider); // 开始定位
            isLocationEnable = true;
        } else { // 定位提供者暂不可用
            tv_location.setText(providerMap.get(bestProvider) + "不可用");
            isLocationEnable = false;
        }
    }

    // 显示定位结果文本
    private void showLocation(Location location) {
        if (location != null) {
            // 创建一个根据经纬度查询详细地址的任务
            GetAddressTask task = new GetAddressTask(this, location, address -> {
                String desc = String.format("%s\n定位信息如下： " +
                                "\n\t定位时间为%s，" + "\n\t经度为%f，纬度为%f，" +
                                "\n\t高度为%d米，精度为%d米，" +
                                "\n\t详细地址为%s。",
                        mLocationDesc, DateUtil.formatDate(location.getTime()),
                        location.getLongitude(), location.getLatitude(),
                        Math.round(location.getAltitude()), Math.round(location.getAccuracy()),
                        address);
                tv_location.setText(desc);
            });
            task.start(); // 启动地址查询任务
        } else {
            tv_location.setText(mLocationDesc + "\n暂未获取到定位对象");
        }
    }

    // 开始定位
    private void beginLocation(String method) {
        // 检查当前设备是否已经开启了定位功能
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "请授予定位权限并开启定位功能", Toast.LENGTH_SHORT).show();
            return;
        }
        // 设置定位管理器的位置变更监听器
        mLocationMgr.requestLocationUpdates(method, 300, 0, mLocationListener);
        // 获取最后一次成功定位的位置信息
        Location location = mLocationMgr.getLastKnownLocation(method);
        showLocation(location); // 显示定位结果文本
    }

    // 定义一个位置变更监听器
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location); // 显示定位结果文本
        }

        @Override
        public void onProviderDisabled(String arg0) {}

        @Override
        public void onProviderEnabled(String arg0) {}

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
    };

    // 定义一个刷新任务，若无法定位则每隔一秒就尝试定位
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            if (!isLocationEnable) {
                initLocation(); // 初始化定位服务
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationMgr.removeUpdates(mLocationListener); // 移除定位管理器的位置变更监听器
    }

}