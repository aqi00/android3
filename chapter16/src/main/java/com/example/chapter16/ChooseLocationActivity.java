package com.example.chapter16;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.object.param.Geo2AddressParam;
import com.tencent.lbssearch.object.result.Geo2AddressResultObject;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.map.tools.net.http.HttpResponseListener;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptor;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;

public class ChooseLocationActivity extends AppCompatActivity implements
        TencentLocationListener, TencentMap.OnMapClickListener,
        TencentMap.OnMarkerDragListener, TencentMap.OnCameraChangeListener {
    private final static String TAG = "ChooseLocationActivity";
    private TencentLocationManager mLocationManager; // 声明一个腾讯定位管理器对象
    private MapView mMapView; // 声明一个地图视图对象
    private TencentMap mTencentMap; // 声明一个腾讯地图对象
    private boolean isFirstLoc = true; // 是否首次定位
    private float mZoom=12; // 缩放级别
    private LatLng mMyPos; // 当前的经纬度
    private String mAddress; // 详细地址

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);
        initLocation(); // 初始化定位服务
        findViewById(R.id.btn_next).setOnClickListener(v -> gotoNext());
    }

    // 初始化定位服务
    private void initLocation() {
        mMapView = findViewById(R.id.mapView);
        mTencentMap = mMapView.getMap(); // 获取腾讯地图对象
        mTencentMap.setOnMapClickListener(this); // 设置地图的点击监听器
        mTencentMap.setOnMarkerDragListener(this); // 设置地图标记的拖动监听器
        mTencentMap.setOnCameraChangeListener(this); // 设置相机视角的变更监听器
        mLocationManager = TencentLocationManager.getInstance(this);
        // 创建腾讯定位请求对象
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setInterval(30000).setAllowGPS(true);
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA);
        mLocationManager.requestLocationUpdates(request, this); // 开始定位监听
    }

    // 跳到个人信息填写页面
    private void gotoNext() {
        if (mMyPos == null) {
            Toast.makeText(this, "请先选择您的常住地点", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, InfoEditActivity.class);
        intent.putExtra("latitude", mMyPos.latitude);
        intent.putExtra("longitude", mMyPos.longitude);
        intent.putExtra("address", mAddress);
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(TencentLocation location, int resultCode, String resultDesc) {
        if (resultCode == TencentLocation.ERROR_OK) { // 定位成功
            if (location != null && isFirstLoc) { // 首次定位
                isFirstLoc = false;
                // 创建一个经纬度对象
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                moveLocation(latLng, location.getAddress()); // 将地图移动到当前位置
            }
        } else { // 定位失败
            Log.d(TAG, "定位失败，错误代码为"+resultCode+"，错误描述为"+resultDesc);
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {}

    // 将地图移动到当前位置
    private void moveLocation(LatLng latLng, String address) {
        mMyPos = latLng;
        mAddress = address;
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, mZoom);
        mTencentMap.moveCamera(update); // 把相机视角移动到指定地点
        // 从指定视图中获取位图描述
        BitmapDescriptor bitmapDesc = BitmapDescriptorFactory
                .fromView(getMarkerView(mAddress));
        MarkerOptions marker = new MarkerOptions(latLng).draggable(true) // 可以拖动
                .visible(true).icon(bitmapDesc).snippet("这是您的当前位置");
        mTencentMap.addMarker(marker); // 往地图添加标记
    }

    // 获取标记视图
    private View getMarkerView(String address) {
        View view = getLayoutInflater().inflate(R.layout.marker_me, null);
        TextView tv_address = view.findViewById(R.id.tv_address);
        tv_address.setText(address);
        return view;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mTencentMap.clearAllOverlays(); // 清除所有覆盖物
        mMyPos = latLng;
        // 创建一个腾讯搜索对象
        TencentSearch tencentSearch = new TencentSearch(this);
        Geo2AddressParam param = new Geo2AddressParam(mMyPos);
        // 根据经纬度查询地图上的详细地址
        tencentSearch.geo2address(param, new HttpResponseListener() {
            @Override
            public void onSuccess(int i, Object o) {
                Geo2AddressResultObject result = (Geo2AddressResultObject) o;
                String address = String.format("%s（%s）",
                        result.result.address, result.result.formatted_addresses.recommend);
                moveLocation(mMyPos, address); // 将地图移动到当前位置
            }

            @Override
            public void onFailure(int i, String s, Throwable throwable) {
                Log.d(TAG, "geo2address onFailure code="+i+", msg="+s);
            }
        });
    }

    @Override
    public void onMarkerDragStart(Marker marker) {}

    @Override
    public void onMarkerDrag(Marker marker) {}

    @Override
    public void onMarkerDragEnd(Marker marker) {
        onMapClick(marker.getPosition()); // 触发该位置的地图点击事件
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {}

    @Override
    public void onCameraChangeFinished(CameraPosition cameraPosition) {
        mZoom = cameraPosition.zoom;
    }

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