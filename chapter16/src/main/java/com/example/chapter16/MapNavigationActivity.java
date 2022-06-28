package com.example.chapter16;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.param.DrivingParam;
import com.tencent.lbssearch.object.param.WalkingParam;
import com.tencent.lbssearch.object.result.DrivingResultObject;
import com.tencent.lbssearch.object.result.WalkingResultObject;
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
import com.tencent.tencentmap.mapsdk.maps.model.LatLngBounds;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;
import com.tencent.tencentmap.mapsdk.vector.utils.animation.MarkerTranslateAnimator;

import java.util.ArrayList;
import java.util.List;

public class MapNavigationActivity extends AppCompatActivity
        implements TencentLocationListener, TencentMap.OnMapClickListener {
    private final static String TAG = "MapNavigationActivity";
    private RadioGroup rg_type; // 声明一个单选组对象
    private TencentLocationManager mLocationManager; // 声明一个腾讯定位管理器对象
    private MapView mMapView; // 声明一个地图视图对象
    private TencentMap mTencentMap; // 声明一个腾讯地图对象
    private boolean isFirstLoc = true; // 是否首次定位
    private LatLng mMyPos; // 当前的经纬度
    private List<LatLng> mPosList = new ArrayList<>(); // 起点和终点
    private List<LatLng> mRouteList = new ArrayList<>(); // 导航路线列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_navigation);
        initLocation(); // 初始化定位服务
        initView(); // 初始化视图
    }

    // 初始化视图
    private void initView() {
        rg_type = findViewById(R.id.rg_type);
        rg_type.setOnCheckedChangeListener((group, checkedId) -> showRoute());
        findViewById(R.id.btn_start).setOnClickListener(v -> {
            if (mPosList.size() < 2) {
                Toast.makeText(this, "请选中起点和终点后再出发", Toast.LENGTH_SHORT).show();
            } else {
                playDriveAnim(); // 播放行驶过程动画
            }
        });
        findViewById(R.id.btn_reset).setOnClickListener(v -> {
            mTencentMap.clearAllOverlays(); // 清除所有覆盖物
            mPosList.clear();
            mRouteList.clear();
            showMyMarker(); // 显示我的位置标记
        });
    }

    private Marker mMarker; // 声明一个小车标记
    // 播放行驶过程动画
    private void playDriveAnim() {
        if (mPosList.size() < 2) {
            return;
        }
        if (mMarker != null) {
            mMarker.remove(); // 移除地图标记
        }
        // 从指定图片中获取位图描述
        BitmapDescriptor bitmapDesc = BitmapDescriptorFactory.fromResource(R.drawable.car);
        MarkerOptions ooMarker = new MarkerOptions(mRouteList.get(0))
                .anchor(0.5f, 0.5f).icon(bitmapDesc).flat(true).clockwise(false);
        mMarker = mTencentMap.addMarker(ooMarker); // 往地图添加标记
        LatLng[] routeArray = mRouteList.toArray(new LatLng[mRouteList.size()]);
        // 创建平移动画
        MarkerTranslateAnimator anim = new MarkerTranslateAnimator(mMarker, 50 * 1000, routeArray, true);
        // 动态调整相机视角
        mTencentMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                LatLngBounds.builder().include(mRouteList).build(), 50));
        anim.startAnimation(); // 开始播放动画
    }

    // 初始化定位服务
    private void initLocation() {
        mMapView = findViewById(R.id.mapView);
        mTencentMap = mMapView.getMap(); // 获取腾讯地图对象
        mTencentMap.setOnMapClickListener(this); // 设置地图的点击监听器
        mLocationManager = TencentLocationManager.getInstance(this);
        // 创建腾讯定位请求对象
        TencentLocationRequest request = TencentLocationRequest.create();
        request.setInterval(30000).setAllowGPS(true);
        request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA);
        mLocationManager.requestLocationUpdates(request, this); // 开始定位监听
    }

    // 显示我的位置标记
    private void showMyMarker() {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(mMyPos, 12);
        mTencentMap.moveCamera(update); // 把相机视角移动到指定地点
        showPosMarker(mMyPos, R.drawable.icon_locate, "这是您的当前位置"); // 显示位置标记
    }

    // 显示位置标记
    private void showPosMarker(LatLng latLng, int imageId, String desc) {
        // 从指定图片中获取位图描述
        BitmapDescriptor bitmapDesc = BitmapDescriptorFactory.fromResource(imageId);
        MarkerOptions ooMarker = new MarkerOptions(latLng).draggable(false) // 不可拖动
                .visible(true).icon(bitmapDesc).snippet(desc);
        mTencentMap.addMarker(ooMarker); // 往地图添加标记
    }

    @Override
    public void onLocationChanged(TencentLocation location, int resultCode, String resultDesc) {
        if (resultCode == TencentLocation.ERROR_OK) { // 定位成功
            if (location != null && isFirstLoc) { // 首次定位
                isFirstLoc = false;
                // 创建一个经纬度对象
                mMyPos = new LatLng(location.getLatitude(), location.getLongitude());
                showMyMarker(); // 显示我的位置标记
            }
        } else { // 定位失败
            Log.d(TAG, "定位失败，错误代码为"+resultCode+"，错误描述为"+resultDesc);
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {}

    @Override
    public void onMapClick(LatLng latLng) {
        mPosList.add(latLng);
        if (mPosList.size() == 1) {
            showPosMarker(latLng, R.drawable.icon_geo, "起点"); // 显示位置标记
        }
        showRoute(); // 展示导航路线
    }

    // 展示导航路线
    private void showRoute() {
        if (mPosList.size() >= 2) {
            mRouteList.clear();
            LatLng beginPos = mPosList.get(0); // 获取起点
            LatLng endPos = mPosList.get(mPosList.size()-1); // 获取终点
            mTencentMap.clearAllOverlays(); // 清除所有覆盖物
            showPosMarker(beginPos, R.drawable.icon_geo, "起点"); // 显示位置标记
            showPosMarker(endPos, R.drawable.icon_geo, "终点"); // 显示位置标记
            if (rg_type.getCheckedRadioButtonId() == R.id.rb_walk) {
                getWalkingRoute(beginPos, endPos); // 规划步行导航
            } else {
                getDrivingRoute(beginPos, endPos); // 规划行车导航
            }
        }
    }

    // 规划步行导航
    private void getWalkingRoute(LatLng beginPos, LatLng endPos) {
        WalkingParam walkingParam = new WalkingParam();
        walkingParam.from(beginPos); // 指定步行的起点
        walkingParam.to(endPos); // 指定步行的终点
        // 创建一个腾讯搜索对象
        TencentSearch tencentSearch = new TencentSearch(getApplicationContext());
        Log.d(TAG, "checkParams:" + walkingParam.checkParams());
        // 根据步行参数规划导航路线
        tencentSearch.getRoutePlan(walkingParam, new HttpResponseListener<WalkingResultObject>() {
            @Override
            public void onSuccess(int statusCode, WalkingResultObject object) {
                if (object==null || object.result==null || object.result.routes==null) {
                    Log.d(TAG, "导航路线为空");
                    return;
                }
                Log.d(TAG, "message:" + object.message);
                for (WalkingResultObject.Route result : object.result.routes) {
                    mRouteList.addAll(result.polyline);
                    // 往地图上添加一组连线
                    mTencentMap.addPolyline(new PolylineOptions().addAll(mRouteList)
                            .color(0x880000ff).width(20));
                }
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable) {
                Log.d(TAG, statusCode + "  " + responseString);
            }
        });
    }

    // 规划行车导航
    private void getDrivingRoute(LatLng beginPos, LatLng endPos) {
        // 创建导航参数
        DrivingParam drivingParam = new DrivingParam(beginPos, endPos);
        // 指定道路类型为主路
        drivingParam.roadType(DrivingParam.RoadType.ON_MAIN_ROAD);
        drivingParam.heading(90); // 起点位置的车头方向
        drivingParam.accuracy(5); // 行车导航的精度，单位米
        // 创建一个腾讯搜索对象
        TencentSearch tencentSearch = new TencentSearch(this);
        // 根据行车参数规划导航路线
        tencentSearch.getRoutePlan(drivingParam, new HttpResponseListener<DrivingResultObject>() {

            @Override
            public void onSuccess(int statusCode, DrivingResultObject object) {
                if (object==null || object.result==null || object.result.routes==null) {
                    Log.d(TAG, "导航路线为空");
                    return;
                }
                Log.d(TAG, "message:" + object.message);
                for (DrivingResultObject.Route route : object.result.routes){
                    mRouteList.addAll(route.polyline);
                    // 往地图上添加一组连线
                    mTencentMap.addPolyline(new PolylineOptions().addAll(mRouteList)
                            .color(0x880000ff).width(20));
                }
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable) {
                Log.d(TAG, statusCode + "  " + responseString);
            }
        });
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