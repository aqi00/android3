package com.example.chapter16;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptor;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;

public class MapBasicActivity extends AppCompatActivity implements TencentLocationListener {
    private final static String TAG = "MapBasicActivity";
    private TencentLocationManager mLocationManager; // 声明一个腾讯定位管理器对象
    private MapView mMapView; // 声明一个地图视图对象
    private TencentMap mTencentMap; // 声明一个腾讯地图对象
    private boolean isFirstLoc = true; // 是否首次定位

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_basic);
        initLocation(); // 初始化定位服务
        initView(); // 初始化视图
    }

    // 初始化视图
    private void initView() {
        RadioGroup rg_type = findViewById(R.id.rg_type);
        rg_type.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_common) {
                mTencentMap.setMapType(TencentMap.MAP_TYPE_NORMAL); // 设置普通地图
            } else if (checkedId == R.id.rb_satellite) {
                mTencentMap.setMapType(TencentMap.MAP_TYPE_SATELLITE); // 设置卫星地图
            }
        });
        CheckBox ck_traffic = findViewById(R.id.ck_traffic);
        ck_traffic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mTencentMap.setTrafficEnabled(isChecked); // 是否显示交通拥堵状况
        });
    }

    // 初始化定位服务
    private void initLocation() {
        mMapView = findViewById(R.id.mapView);
        mTencentMap = mMapView.getMap(); // 获取腾讯地图对象
        mLocationManager = TencentLocationManager.getInstance(this);
        // 创建腾讯定位请求对象
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setInterval(30000).setAllowGPS(true);
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA);
        mLocationManager.requestLocationUpdates(request, this); // 开始定位监听
    }

    @Override
    public void onLocationChanged(TencentLocation location, int resultCode, String resultDesc) {
        if (resultCode == TencentLocation.ERROR_OK) { // 定位成功
            if (location != null && isFirstLoc) { // 首次定位
                isFirstLoc = false;
                // 创建一个经纬度对象
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 12);
                mTencentMap.moveCamera(update); // 把相机视角移动到指定地点
                // 从指定图片中获取位图描述
                BitmapDescriptor bitmapDesc = BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_locate);
                MarkerOptions ooMarker = new MarkerOptions(latLng).draggable(false) // 不可拖动
                        .visible(true).icon(bitmapDesc).snippet("这是您的当前位置");
                mTencentMap.addMarker(ooMarker); // 往地图添加标记
            }
        } else { // 定位失败
            Log.d(TAG, "定位失败，错误代码为"+resultCode+"，错误描述为"+resultDesc);
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {}

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationManager.removeUpdates(this); // 移除定位监听
        mMapView.onDestroy();
    }

}