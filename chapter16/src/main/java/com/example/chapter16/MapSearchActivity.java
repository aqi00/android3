package com.example.chapter16;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chapter16.util.MapTencentUtil;
import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.param.SearchParam;
import com.tencent.lbssearch.object.result.SearchResultObject;
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
import com.tencent.tencentmap.mapsdk.maps.model.PolygonOptions;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapSearchActivity extends AppCompatActivity
        implements TencentLocationListener, TencentMap.OnMapClickListener {
    private final static String TAG = "MapSearchActivity";
    private TextView tv_scope_desc; // 声明一个文本视图对象
    private EditText et_searchkey; // 声明一个编辑框对象
    private EditText et_city; // 声明一个编辑框对象
    private int mSearchMethod; // 搜索类型
    private String[] mSearchArray = {"搜城市", "搜周边"};
    private int SEARCH_CITY = 0; // 搜城市
    private int SEARCH_NEARBY = 1; // 搜周边

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);
        initView(); // 初始化视图
        initMethodSpinner(); // 初始化搜索方式下拉框
        initLocation(); // 初始化定位服务
        initSearch(); // 初始化搜索服务
    }

    // 初始化视图
    private void initView() {
        tv_scope_desc = findViewById(R.id.tv_scope_desc);
        et_city = findViewById(R.id.et_city);
        et_searchkey = findViewById(R.id.et_searchkey);
        findViewById(R.id.btn_clear_data).setOnClickListener(v -> {
            et_city.setText("");
            et_searchkey.setText("");
            mTencentMap.clearAllOverlays(); // 清除所有覆盖物
            mPosList.clear();
            isPolygon = false;
        });
    }

    // 初始化搜索方式下拉框
    private void initMethodSpinner() {
        Spinner sp_method = findViewById(R.id.sp_method);
        ArrayAdapter<String> county_adapter = new ArrayAdapter<>(this,
                R.layout.item_select, mSearchArray);
        sp_method.setPrompt("请选择POI搜索方式");
        sp_method.setAdapter(county_adapter);
        sp_method.setOnItemSelectedListener(new MethodSelectedListener());
        sp_method.setSelection(0);
    }

    class MethodSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mSearchMethod = arg2;
            if (mSearchMethod == SEARCH_CITY) {
                tv_scope_desc.setText("市内找");
            } else if (mSearchMethod == SEARCH_NEARBY) {
                tv_scope_desc.setText("米内找");
            }
            et_city.setText("");
            et_searchkey.setText("");
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 以下是定位代码
    private TencentLocationManager mLocationManager; // 声明一个腾讯定位管理器对象
    private MapView mMapView; // 声明一个地图视图对象
    private TencentMap mTencentMap; // 声明一个腾讯地图对象
    private boolean isFirstLoc = true; // 是否首次定位
    private LatLng mLatLng; // 当前位置的经纬度

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

    @Override
    public void onLocationChanged(TencentLocation location, int resultCode, String resultDesc) {
        if (resultCode == TencentLocation.ERROR_OK) { // 定位成功
            if (location != null && isFirstLoc) { // 首次定位
                isFirstLoc = false;
                // 创建一个经纬度对象
                mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(mLatLng, 12);
                mTencentMap.moveCamera(update); // 把相机视角移动到指定地点
                // 从指定图片中获取位图描述
                BitmapDescriptor bitmapDesc = BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_locate);
                MarkerOptions ooMarker = new MarkerOptions(mLatLng).draggable(false) // 不可拖动
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

    // 以下是POI搜索代码
    private TencentSearch mTencentSearch; // 声明一个腾讯搜索对象
    private int mLoadIndex = 1; // 搜索结果的第几页

    // 初始化搜索服务
    private void initSearch() {
        // 创建一个腾讯搜索对象
        mTencentSearch = new TencentSearch(this);
        findViewById(R.id.btn_search).setOnClickListener(v -> searchPoi());
        findViewById(R.id.btn_next_data).setOnClickListener(v -> {
            mLoadIndex++;
            mTencentMap.clearAllOverlays(); // 清除所有覆盖物
            searchPoi(); // 搜索指定的地点列表
        });
    }

    // 搜索指定的地点列表
    public void searchPoi() {
        Log.d(TAG, "editCity=" + et_city.getText().toString()
                + ", editSearchKey=" + et_searchkey.getText().toString()
                + ", mLoadIndex=" + mLoadIndex);
        String keyword = et_searchkey.getText().toString();
        String value = et_city.getText().toString();
        SearchParam searchParam = new SearchParam();
        if (mSearchMethod == SEARCH_CITY) { // 城市搜索
            SearchParam.Region region = new SearchParam
                    .Region(value) // 设置搜索城市
                    .autoExtend(false); // 设置搜索范围不扩大
            searchParam = new SearchParam(keyword, region); // 构建地点检索
        } else if (mSearchMethod == SEARCH_NEARBY) { // 周边搜索
            int radius = Integer.parseInt(value);
            SearchParam.Nearby nearby = new SearchParam
                    .Nearby(mLatLng, radius).autoExtend(false); // 不扩大搜索范围
            searchParam = new SearchParam(keyword, nearby); // 构建地点检索
        }
        searchParam.pageSize(10); // 每页大小
        searchParam.pageIndex(mLoadIndex); // 第几页
        // 根据搜索参数查找符合条件的地点列表
        mTencentSearch.search(searchParam, new HttpResponseListener<BaseObject>() {

            @Override
            public void onFailure(int arg0, String arg2, Throwable arg3) {
                Toast.makeText(getApplicationContext(), arg2, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int arg0, BaseObject arg1) {
                if (arg1 == null) {
                    return;
                }
                SearchResultObject obj = (SearchResultObject) arg1;
                if(obj.data==null || obj.data.size()==0){
                    return;
                }
                // 将地图中心坐标移动到检索到的第一个地点
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(obj.data.get(0).latLng, 12);
                mTencentMap.moveCamera(update); // 把相机视角移动到指定地点
                // 将其他检索到的地点在地图上用 marker 标出来
                for (SearchResultObject.SearchResultData data : obj.data){
                    Log.d(TAG,"title:"+data.title + ";" + data.address);
                    // 往地图添加标记
                    mTencentMap.addMarker(new MarkerOptions(data.latLng)
                            .title(data.title).snippet(data.address));
                }
            }
        });
    }

    // 下面是绘图代码
    private int lineColor = 0x55FF0000;
    private int textColor = 0x990000FF;
    private int polygonColor = 0x77FFFF00;
    private int radiusLimit = 100;
    private List<LatLng> mPosList = new ArrayList<>();
    private boolean isPolygon = false;

    // 往地图上添加一个点
    private void addDot(LatLng pos) {
        if (isPolygon) {
            mPosList.clear();
            isPolygon = false;
        }
        boolean isFirst = false;
        LatLng thisPos = pos;
        if (mPosList.size() > 0) {
            LatLng firstPos = mPosList.get(0);
            int distance = (int) Math.round(MapTencentUtil.getShortDistance(
                    thisPos.longitude, thisPos.latitude,
                    firstPos.longitude, firstPos.latitude));
            if (mPosList.size() == 1 && distance <= 0) { // 多次点击起点，要忽略之
                return;
            } else if (mPosList.size() > 1) {
                LatLng lastPos = mPosList.get(mPosList.size() - 1);
                int lastDistance = (int) Math.round(MapTencentUtil.getShortDistance(
                        thisPos.longitude, thisPos.latitude,
                        lastPos.longitude, lastPos.latitude));
                if (lastDistance <= 0) { // 重复响应当前位置的点击，要忽略之
                    return;
                }
            }
            if (distance < radiusLimit * 2) {
                thisPos = firstPos;
                isFirst = true;
            }
            Log.d(TAG, "distance=" + distance + ", radiusLimit=" + radiusLimit + ", isFirst=" + isFirst);

            // 画直线
            LatLng lastPos = mPosList.get(mPosList.size() - 1);
            List<LatLng> pointList = new ArrayList<>();
            pointList.add(lastPos);
            pointList.add(thisPos);
            PolylineOptions ooPolyline = new PolylineOptions().width(2)
                    .color(lineColor).addAll(pointList);
            // 下面计算两点之间距离
            distance = (int) Math.round(MapTencentUtil.getShortDistance(
                    thisPos.longitude, thisPos.latitude,
                    lastPos.longitude, lastPos.latitude));
            String disText;
            if (distance > 1000) {
                disText = Math.round(distance * 10 / 1000) / 10d + "公里";
            } else {
                disText = distance + "米";
            }
            PolylineOptions.SegmentText segment = new PolylineOptions.SegmentText(0, 1, disText);
            PolylineOptions.Text text = new PolylineOptions.Text.Builder(segment)
                    .color(textColor).size(15).build();
            ooPolyline.text(text);
            mTencentMap.addPolyline(ooPolyline); // 往地图上添加一组连线
        }
        if (!isFirst) {
            // 从指定图片中获取位图描述
            BitmapDescriptor bitmapDesc = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
            MarkerOptions ooMarker = new MarkerOptions(thisPos).draggable(false) // 不可拖动
                    .visible(true).icon(bitmapDesc);
            mTencentMap.addMarker(ooMarker); // 往地图添加标记
            // 设置地图标记的点击监听器
            mTencentMap.setOnMarkerClickListener(marker -> {
                LatLng markPos = marker.getPosition();
                addDot(markPos); // 往地图上添加一个点
                marker.showInfoWindow(); // 显示标记的信息窗口
                return true;
            });
        } else {
            if (mPosList.size() < 3) { // 可能存在地图与标记同时响应点击事件的情况
                mPosList.clear();
                isPolygon = false;
                return;
            }
            // 画多边形
            PolygonOptions ooPolygon = new PolygonOptions().addAll(mPosList)
                    .strokeColor(0xFF00FF00).strokeWidth(3)
                    .fillColor(polygonColor);
            mTencentMap.addPolygon(ooPolygon); // 往地图上添加多边形
            isPolygon = true;
        }
        mPosList.add(thisPos);
    }

    @Override
    public void onMapClick(LatLng arg0) {
        addDot(arg0); // 往地图上添加一个点
    }

}