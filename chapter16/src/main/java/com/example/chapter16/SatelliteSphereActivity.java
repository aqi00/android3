package com.example.chapter16;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.chapter16.bean.Satellite;
import com.example.chapter16.util.DateUtil;
import com.example.chapter16.util.SwitchUtil;
import com.example.chapter16.widget.CompassView;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("DefaultLocale")
public class SatelliteSphereActivity extends AppCompatActivity {
    private final static String TAG = "SatelliteSphereActivity";
    private Map<String, String> providerMap = new HashMap<>(); // 定位提供者映射
    private TextView tv_satellite; // 声明一个文本视图对象
    private CompassView cv_satellite; // 声明一个罗盘视图对象
    private Map<Integer, Satellite> mapSatellite = new HashMap<>(); // 导航卫星映射
    private LocationManager mLocationMgr; // 声明一个定位管理器对象
    private Criteria mCriteria = new Criteria(); // 声明一个定位准则对象
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private boolean isLocationEnable = false; // 定位服务是否可用
    private String mLocationType = ""; // 定位类型。是卫星定位还是网络定位

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satellite_sphere);
        providerMap.put("gps", "卫星");
        providerMap.put("network", "网络");
        tv_satellite = findViewById(R.id.tv_satellite);
        cv_satellite = findViewById(R.id.cv_satellite);
        SwitchUtil.checkLocationIsOpen(this, "需要打开定位功能才能查看卫星导航信息");
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
        // 设置定位精确度。Criteria.ACCURACY_COARSE表示粗略，Criteria.ACCURACY_FIN表示精细
        mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        mCriteria.setAltitudeRequired(true); // 设置是否需要海拔信息
        mCriteria.setBearingRequired(true); // 设置是否需要方位信息
        mCriteria.setCostAllowed(true); // 设置是否允许运营商收费
        mCriteria.setPowerRequirement(Criteria.POWER_LOW); // 设置对电源的需求
        // 获取定位管理器的最佳定位提供者
        String bestProvider = mLocationMgr.getBestProvider(mCriteria, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 实测发现部分手机的android11系统使用卫星定位会没返回
            bestProvider = "network";
        }
        if (mLocationMgr.isProviderEnabled(bestProvider)) {  // 定位提供者当前可用
            mLocationType = providerMap.get(bestProvider)+"定位";
            beginLocation(bestProvider); // 开始定位
            isLocationEnable = true;
        } else { // 定位提供者暂不可用
            isLocationEnable = false;
        }
    }

    // 设置定位结果文本
    private void showLocation(Location location) {
        if (location != null) {
            String desc = String.format("当前定位类型：%s\n定位时间：%s" +
                            "\n经度：%f，纬度：%f\n高度：%d米，精度：%d米",
                    mLocationType, DateUtil.formatDate(location.getTime()),
                    location.getLongitude(), location.getLatitude(),
                    Math.round(location.getAltitude()), Math.round(location.getAccuracy()));
            tv_satellite.setText(desc);
        } else {
            Log.d(TAG, "暂未获取到定位对象");
        }
    }

    // 开始定位
    private void beginLocation(String method) {
        // 检查当前设备是否已经开启了定位功能
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "请授予定位权限并开启定位功能", Toast.LENGTH_SHORT).show();
            return;
        }
        // 设置定位管理器的位置变更监听器
        mLocationMgr.requestLocationUpdates(method, 300, 0, mLocationListener);
        // 获取最后一次成功定位的位置信息
        Location location = mLocationMgr.getLastKnownLocation(method);
        showLocation(location); // 显示定位结果文本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 注册全球导航卫星系统的状态监听器
            mLocationMgr.registerGnssStatusCallback(mGnssStatusListener, null);
        } else {
            // 给定位管理器添加导航状态监听器
            mLocationMgr.addGpsStatusListener(mStatusListener);
        }
    }

    private String[] mSystemArray = new String[] {"UNKNOWN", "GPS", "SBAS",
            "GLONASS", "QZSS", "BEIDOU", "GALILEO", "IRNSS"};
    @RequiresApi(api = Build.VERSION_CODES.N)
    // 定义一个GNSS状态监听器
    private GnssStatus.Callback mGnssStatusListener = new GnssStatus.Callback() {
        @Override
        public void onStarted() {}

        @Override
        public void onStopped() {}

        @Override
        public void onFirstFix(int ttffMillis) {}

        // 在卫星导航系统的状态变更时触发
        @Override
        public void onSatelliteStatusChanged(GnssStatus status) {
            mapSatellite.clear();
            for (int i=0; i<status.getSatelliteCount(); i++) {
                Log.d(TAG, "i="+i+",getSvid="+status.getSvid(i)+",getConstellationType="+status.getConstellationType(i));
                Satellite item = new Satellite(); // 创建一个卫星信息对象
                item.signal = status.getCn0DbHz(i); // 获取卫星的信号
                item.elevation = status.getElevationDegrees(i); // 获取卫星的仰角
                item.azimuth = status.getAzimuthDegrees(i); // 获取卫星的方位角
                item.time = DateUtil.getNowDateTime(); // 获取当前时间
                int systemType = status.getConstellationType(i); // 获取卫星的类型
                item.name = mSystemArray[systemType];
                mapSatellite.put(i, item);
            }
            cv_satellite.setSatelliteMap(mapSatellite); // 设置卫星浑天仪
        }
    };

    // 定义一个位置变更监听器
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location); // 显示定位结果文本
        }

        @Override
        public void onProviderDisabled(String arg0) {
        }

        @Override
        public void onProviderEnabled(String arg0) {
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        }
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
        if (mLocationMgr != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // 注销全球导航卫星系统的状态监听器
                mLocationMgr.unregisterGnssStatusCallback(mGnssStatusListener);
            } else {
                // 移除定位管理器的导航状态监听器
                mLocationMgr.removeGpsStatusListener(mStatusListener);
            }
            // 移除定位管理器的位置变更监听器
            mLocationMgr.removeUpdates(mLocationListener);
        }
    }

    // 定义一个导航状态监听器
    private GpsStatus.Listener mStatusListener = new GpsStatus.Listener() {
        // 在卫星导航系统的状态变更时触发
        @Override
        public void onGpsStatusChanged(int event) {
            if (ActivityCompat.checkSelfPermission(SatelliteSphereActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // 获取卫星定位的状态信息
                GpsStatus gpsStatus = mLocationMgr.getGpsStatus(null);
                switch (event) {
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS: // 周期性报告卫星状态
                        // 得到所有收到的卫星的信息，包括 卫星的高度角、方位角、信噪比、和伪随机号（及卫星编号）
                        Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
                        for (GpsSatellite satellite : satellites) {
                            /*
                             * satellite.getElevation(); //卫星的仰角 (卫星的高度)
                             * satellite.getAzimuth(); //卫星的方位角
                             * satellite.getSnr(); //卫星的信噪比
                             * satellite.getPrn(); //卫星的伪随机码，可以认为就是卫星的编号
                             * satellite.hasAlmanac(); //卫星是否有年历表
                             * satellite.hasEphemeris(); //卫星是否有星历表
                             * satellite.usedInFix(); //卫星是否被用于近期的GPS修正计算
                             */
                            Satellite item = new Satellite(); // 创建一个卫星信息对象
                            int prn_id = satellite.getPrn(); // 获取卫星的编号
                            item.signal = Math.round(satellite.getSnr()); // 获取卫星的信号
                            item.elevation = Math.round(satellite.getElevation()); // 获取卫星的仰角
                            item.azimuth = Math.round(satellite.getAzimuth()); // 获取卫星的方位角
                            item.time = DateUtil.getNowDateTime(); // 获取当前时间
                            if (prn_id <= 51) { // 美国的GPS
                                item.name = "GPS";
                            } else if (prn_id >= 201 && prn_id <= 235) { // 中国的北斗
                                item.name = "BEIDOU";
                            } else if (prn_id >= 65 && prn_id <= 96) { // 俄罗斯的格洛纳斯
                                item.name = "GLONASS";
                            } else if (prn_id >= 301 && prn_id <= 336) { // 欧洲的伽利略
                                item.name = "GALILEO";
                            } else {
                                item.name = "未知";
                            }
                            Log.d(TAG, "id="+prn_id+", signal="+item.signal+", elevation="+item.elevation+", azimuth="+item.azimuth);
                            mapSatellite.put(prn_id, item);
                        }
                        cv_satellite.setSatelliteMap(mapSatellite); // 设置卫星浑天仪
                    case GpsStatus.GPS_EVENT_FIRST_FIX: // 首次卫星定位
                    case GpsStatus.GPS_EVENT_STARTED: // 卫星导航服务开始
                    case GpsStatus.GPS_EVENT_STOPPED: // 卫星导航服务停止
                    default:
                        break;
                }
            }
        }
    };

}
