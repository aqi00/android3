package com.example.chapter14;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.chapter14.adapter.VideoPagerAdapter;
import com.example.chapter14.bean.VideoInfo;
import com.example.chapter14.task.VideoLoadTask;

import java.util.ArrayList;
import java.util.List;

public class ShortViewActivity extends AppCompatActivity {
    private SwipeRefreshLayout srl_dynamic; // 声明一个下拉刷新布局对象
    private List<VideoInfo> mVideoList = new ArrayList<>(); // 声明一个地址列表
    private VideoPagerAdapter mAdapter; // 声明一个视频翻页适配器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_view);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕常亮
        initView(); // 初始化视图
        startLoad(); // 开始加载短视频
    }

    // 初始化视图
    private void initView() {
        srl_dynamic = findViewById(R.id.srl_dynamic);
        ViewPager2 vp2_content = findViewById(R.id.vp2_content);
        srl_dynamic.setOnRefreshListener(() -> startLoad()); // 设置下拉布局的下拉刷新监听器
        // 构建一个视频地址的翻页适配器
        mAdapter = new VideoPagerAdapter(this, mVideoList);
        vp2_content.setAdapter(mAdapter); // 设置二代翻页视图的适配器
        findViewById(R.id.iv_add).setOnClickListener(v ->
                startActivity(new Intent(this, ShortTakeActivity.class)));
    }

    // 开始加载最新的短视频
    private void startLoad() {
        // 创建一个视频列表加载任务
        VideoLoadTask task = new VideoLoadTask(this, videoList -> {
            srl_dynamic.setRefreshing(false); // 结束下拉刷新布局的刷新动作
            mVideoList.clear();
            mVideoList.addAll(videoList);
            mAdapter.notifyDataSetChanged(); // 通知适配器数据发生变化
        });
        task.start(); // 启动短视频列表加载任务
    }

}