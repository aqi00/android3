package com.example.chapter16;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.chapter16.bean.PersonInfo;
import com.example.chapter16.constant.UrlConstant;
import com.example.chapter16.task.NearbyLoadTask;
import com.example.chapter16.widget.PersonDialog;
import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.object.param.DrivingParam;
import com.tencent.lbssearch.object.result.DrivingResultObject;
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
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class NearbyActivity extends AppCompatActivity implements
        TencentLocationListener, PersonDialog.PersonCallBack {
    private final static String TAG = "NearbyActivity";
    private TencentLocationManager mLocationManager; // 声明一个腾讯定位管理器对象
    private MapView mMapView; // 声明一个地图视图对象
    private TencentMap mTencentMap; // 声明一个腾讯地图对象
    private boolean isFirstLoc = true; // 是否首次定位
    private int mSexSeq=0, mLoveSeq=0; // 性别序号，爱好序号
    private List<PersonInfo> mPersonList = new ArrayList<>(); // 附近人员列表
    private LatLng mMyPos; // 当前的经纬度
    private List<LatLng> mRouteList = new ArrayList<>(); // 导航路线列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕常亮
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("附近的人");
        initSexSpinner(); // 初始化性别下拉框
        initLoveSpinner(); // 初始化爱好下拉框
        initLocation(); // 初始化定位服务
    }

    // 初始化性别下拉框
    private void initSexSpinner() {
        Spinner sp_sex = findViewById(R.id.sp_sex);
        ArrayAdapter<String> sex_adapter = new ArrayAdapter<>(this,
                R.layout.item_select, sexArray);
        sp_sex.setPrompt("请选择要寻找的人群");
        sp_sex.setAdapter(sex_adapter);
        sp_sex.setOnItemSelectedListener(new SexSelectedListener());
        sp_sex.setSelection(0);
    }

    private String[] sexArray = {"看所有人", "只看男生", "只看女生"};
    class SexSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mSexSeq = arg2;
            showPersonMarker(mPersonList); // 挑选并显示符合条件的人员标记
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 初始化爱好下拉框
    private void initLoveSpinner() {
        Spinner sp_love = findViewById(R.id.sp_love);
        ArrayAdapter<String> love_adapter = new ArrayAdapter<>(this,
                R.layout.item_select, loveArray);
        sp_love.setPrompt("请选择要寻找的人群");
        sp_love.setAdapter(love_adapter);
        sp_love.setOnItemSelectedListener(new LoveSelectedListener());
        sp_love.setSelection(0);
    }

    private String[] loveArray = {"找所有爱好", "唱歌", "跳舞", "绘画", "弹琴", "摄影", "出售闲置物品"};
    class LoveSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mLoveSeq = arg2;
            showPersonMarker(mPersonList); // 挑选并显示符合条件的人员标记
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 初始化定位服务
    private void initLocation() {
        mMapView = findViewById(R.id.mapView);
        mTencentMap = mMapView.getMap(); // 获取腾讯地图对象
        // 设置地图标记的点击监听器
        mTencentMap.setOnMarkerClickListener(marker -> {
            PersonInfo person = (PersonInfo) marker.getTag();
            PersonDialog dialog = new PersonDialog(this, person, this);
            dialog.show(); // 弹出人员信息对话框
            return true;
        });
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
                mMyPos = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(mMyPos, 12);
                mTencentMap.moveCamera(update); // 把相机视角移动到指定地点
                // 创建一个附近人员列表的加载任务
                NearbyLoadTask task = new NearbyLoadTask(this, personList -> {
                    mPersonList = personList;
                    showPersonMarker(mPersonList); // 挑选并显示符合条件的人员标记
                });
                task.start(); // 启动人员列表加载任务
            }
        } else { // 定位失败
            Log.d(TAG, "定位失败，错误代码为"+resultCode+"，错误描述为"+resultDesc);
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {}

    // 显示我的位置标记
    private void showMyMarker() {
        // 从指定图片中获取位图描述
        BitmapDescriptor bitmapDesc = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_locate);
        MarkerOptions ooMarker = new MarkerOptions(mMyPos).draggable(false) // 不可拖动
                .visible(true).icon(bitmapDesc).snippet("这是您的当前位置");
        mTencentMap.addMarker(ooMarker); // 往地图添加标记
    }

    // 挑选并显示符合条件的人员标记
    private void showPersonMarker(List<PersonInfo> personList) {
        mTencentMap.clearAllOverlays(); // 清除所有覆盖物
        if (mMyPos != null) {
            showMyMarker(); // 显示我的位置标记
        }
        for (PersonInfo person : personList) {
            if (mSexSeq>0 && (mSexSeq-1)!=person.getSex()) {
                continue;
            }
            if (mLoveSeq>0 && !loveArray[mLoveSeq].equals(person.getLove())) {
                continue;
            }
            addNearbyMarker(person); // 显示附近人员的标记
        }
    }

    // 显示附近人员的标记
    private void addNearbyMarker(PersonInfo person) {
        // 使用Glide加载网络图片。因为位图描述只能在获得图片数据后生成，所以必须等待图片加载完成再添加标记
        Glide.with(this).load(UrlConstant.HTTP_PREFIX+person.getFace())
                .circleCrop().into(new CustomTarget<Drawable>() {

            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                LatLng latLng = new LatLng(person.getLatitude(), person.getLongitude());
                // 从指定视图中获取位图描述
                BitmapDescriptor bitmapDesc = BitmapDescriptorFactory
                        .fromView(getMarkerView(person, resource));
                MarkerOptions marker = new MarkerOptions(latLng).draggable(false) // 不可拖动
                        .visible(true).icon(bitmapDesc).tag(person);
                mTencentMap.addMarker(marker); // 往地图添加标记
            }

            @Override
            public void onLoadCleared(Drawable placeholder) {}
        });
    }

    // 获取标记视图。男生标蓝色，女生标粉色
    private View getMarkerView(PersonInfo person, Drawable drawable) {
        View view = getLayoutInflater().inflate(R.layout.marker_person, null);
        TextView tv_name = view.findViewById(R.id.tv_name);
        ImageView iv_face = view.findViewById(R.id.iv_face);
        tv_name.setText(person.getName());
        iv_face.setImageDrawable(drawable);
        int colorResId = person.getSex()==0 ? R.color.blue : R.color.pink;
        tv_name.setTextColor(getResources().getColor(colorResId));
        return view;
    }

    @Override
    public void onDial(PersonInfo person) {
        Intent intent = new Intent(); // 创建一个新意图
        intent.setAction(Intent.ACTION_DIAL); // 设置意图动作为准备拨号
        Uri uri = Uri.parse("tel:" + person.getPhone()); // 声明一个拨号的Uri
        intent.setData(uri); // 设置意图前往的路径
        startActivity(intent); // 启动意图通往的活动页面
    }

    @Override
    public void onNavigate(PersonInfo person) {
        LatLng beginPos = mMyPos;
        LatLng endPos = new LatLng(person.getLatitude(), person.getLongitude());
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
                showPersonMarker(mPersonList);
                mRouteList.clear();
                for (DrivingResultObject.Route route : object.result.routes){
                    mRouteList.addAll(route.polyline);
                    // 往地图上添加一组连线
                    mTencentMap.addPolyline(new PolylineOptions().addAll(mRouteList).color(0x880000ff).width(20));
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