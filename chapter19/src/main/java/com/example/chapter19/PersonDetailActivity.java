package com.example.chapter19;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chapter19.adapter.PortraitRecyclerAdapter;
import com.example.chapter19.dao.PersonDao;
import com.example.chapter19.entity.PersonInfo;
import com.example.chapter19.entity.PersonPortrait;
import com.example.chapter19.task.GetAddressTask;
import com.example.chapter19.util.DateUtil;
import com.example.chapter19.util.SwitchUtil;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("SetTextI18n")
public class PersonDetailActivity extends AppCompatActivity {
    private final static String TAG = "PersonDetailActivity";
    private int VERIFY_CODE = 13; // 人员识别的请求码
    private String mPersonName; // 人员名称
    private TextView tv_name; // 声明一个文本视图对象
    private TextView tv_info; // 声明一个文本视图对象
    private RecyclerView rv_sample; // 声明一个循环视图对象
    private TextView tv_flag; // 声明一个文本视图对象
    private LinearLayout ll_track; // 声明一个线性视图对象
    private TextView tv_time; // 声明一个文本视图对象
    private TextView tv_location; // 声明一个文本视图对象
    private RecyclerView rv_real; // 声明一个循环视图对象
    private Button btn_track; // 声明一个按钮对象
    private PersonDao personDao; // 声明一个人员的持久化对象
    private PersonInfo mPerson; // 人员信息对象
    private LocationManager mLocationMgr; // 声明一个定位管理器对象
    private boolean isLocated = false; // 是否已经定位

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);
        mPersonName = getIntent().getStringExtra("person_name");
        initView(); // 初始化视图
        initData(); // 初始化数据
        SwitchUtil.checkLocationIsOpen(this, "需要打开定位功能才能查看定位信息");
    }

    // 初始化视图
    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("人员详情");
        tv_name = findViewById(R.id.tv_name);
        tv_info = findViewById(R.id.tv_info);
        rv_sample = findViewById(R.id.rv_sample);
        tv_flag = findViewById(R.id.tv_flag);
        ll_track = findViewById(R.id.ll_track);
        tv_time = findViewById(R.id.tv_time);
        tv_location = findViewById(R.id.tv_location);
        rv_real = findViewById(R.id.rv_real);
        btn_track = findViewById(R.id.btn_track);
        btn_track.setOnClickListener(v -> {
            if (mPerson.getFlag() == 0) { // 待识别，则跳到人员识别界面
                gotoVerify(); // 跳到人员识别界面
            } else { // 已识别，则弹出对话框确认是否需要重新识别
                showReverify(mPerson); // 显示是否重新识别的对话框
            }
        });
        // 创建一个水平方向的线性布局管理器
        LinearLayoutManager sampleManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rv_sample.setLayoutManager(sampleManager); // 设置循环视图的布局管理器
        // 创建一个水平方向的线性布局管理器
        LinearLayoutManager realManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rv_real.setLayoutManager(realManager); // 设置循环视图的布局管理器
    }

    // 初始化数据
    private void initData() {
        // 从App实例中获取唯一的人员持久化对象
        personDao = com.example.chapter19.MainApplication.getInstance().getPersonDB().personDao();
        mPerson = personDao.queryPersonByName(mPersonName);
        tv_name.setText("人员姓名："+mPersonName);
        tv_info.setText("人员简介："+mPerson.getInfo());
        // 根据人员名称查询该人员的样本头像列表
        List<PersonPortrait> sampleList = personDao.queryPersonPortrait(mPersonName, 0);
        PortraitRecyclerAdapter sampleAdapter = new PortraitRecyclerAdapter(this, sampleList);
        rv_sample.setAdapter(sampleAdapter);
        showTrackInfo(mPerson); // 显示人员的追踪信息
    }

    // 显示人员的追踪信息
    private void showTrackInfo(PersonInfo person) {
        tv_flag.setText("识别标志："+(person.getFlag()==0?"待识别":"已识别"));
        btn_track.setText(person.getFlag()==0 ? "开始找人" : "重新寻找");
        ll_track.setVisibility(person.getFlag()==0 ? View.GONE : View.VISIBLE);
        if (person.getFlag() != 0) { // 已经识别过了
            tv_time.setText("识别时间："+person.getTime());
            tv_location.setText("识别地点："+person.getLocation());
            // 根据人员名称查询该人员的识别头像列表
            List<PersonPortrait> realList = personDao.queryPersonPortrait(mPersonName, 1);
            PortraitRecyclerAdapter realAdapter = new PortraitRecyclerAdapter(this, realList);
            rv_real.setAdapter(realAdapter);
        }
    }

    // 跳到人员识别界面
    private void gotoVerify() {
        Intent intent = new Intent(this, com.example.chapter19.PersonVerifyActivity.class);
        intent.putExtra("person_name", mPersonName);
        startActivityForResult(intent, VERIFY_CODE);
    }

    // 显示是否重新识别的对话框
    private void showReverify(PersonInfo person) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("请确认")
                .setMessage("你是否要重新寻找"+person.getName())
                .setPositiveButton("是", (dialog, which) -> {
                    person.setTime("");
                    person.setLocation("");
                    person.setFlag(0);
                    // 重新识别，则修改识别标志，并删除已识别的头像
                    personDao.updatePerson(person);
                    personDao.deletePortraitByName(person.getName(), 1);
                    gotoVerify(); // 跳到人员识别界面
                })
                .setNegativeButton("否", null);
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == VERIFY_CODE) {
            List<String> pathList = intent.getStringArrayListExtra("path_list");
            if (pathList!=null && pathList.size()>0) {
                List<PersonPortrait> trackList = new ArrayList<>();
                for (String path : pathList) {
                    String[] paths = path.split("\\|");
                    PersonPortrait portrait = new PersonPortrait(mPersonName, paths[0], 1);
                    portrait.setSimilarity(Double.parseDouble(paths[1]));
                    trackList.add(portrait);
                }
                personDao.insertPortraitList(trackList); // 插入人员头像列表
                updateTrackInfo(mPerson); // 更新人员的追踪信息
            }
        } else {
            mPerson = personDao.queryPersonByName(mPersonName);
            showTrackInfo(mPerson); // 显示人员的追踪信息
        }
    }

    // 更新人员的追踪信息
    private void updateTrackInfo(PersonInfo person) {
        person.setFlag(1); // 识别标志修改为已识别
        person.setTime(DateUtil.getNowFullDateTime());
        personDao.updatePerson(person); // 更新人员信息
        showTrackInfo(person); // 显示人员的追踪信息
        initLocation(); // 开始定位获得当前位置，再更新人员信息表的位置字段
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
            beginLocation(bestProvider); // 开始定位
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

    // 显示定位结果文本
    private void showLocation(Location location) {
        if (location != null) {
            // 创建一个根据经纬度查询详细地址的任务
            GetAddressTask task = new GetAddressTask(this, location, address -> {
                isLocated = true;
                mPerson.setLocation(address);
                personDao.updatePerson(mPerson);
                tv_location.setText("识别地点："+address);
            });
            task.start(); // 启动地址查询任务
        }
    }

    // 定义一个位置变更监听器
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (!isLocated) {
                showLocation(location); // 显示定位结果文本
            }
        }

        @Override
        public void onProviderDisabled(String arg0) {}

        @Override
        public void onProviderEnabled(String arg0) {}

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocationMgr != null) {
            mLocationMgr.removeUpdates(mLocationListener); // 移除定位管理器的位置变更监听器
        }
    }

}