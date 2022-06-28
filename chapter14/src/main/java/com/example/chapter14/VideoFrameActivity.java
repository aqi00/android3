package com.example.chapter14;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter14.util.MediaUtil;

import java.util.ArrayList;
import java.util.List;

public class VideoFrameActivity extends AppCompatActivity {
    private final static String TAG = "VideoFrameActivity";
    private VideoView vv_content; // 声明一个视频视图对象
    private LinearLayout ll_cut; // 声明一个线性视图对象
    private ImageView iv_frame; // 声明一个图像视图对象
    private Uri mUri; // 声明一个Uri地址对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_frame);
        vv_content = findViewById(R.id.vv_content);
        ll_cut = findViewById(R.id.ll_cut);
        iv_frame = findViewById(R.id.iv_frame);
        // 注册一个善后工作的活动结果启动器，获取指定类型的内容
        ActivityResultLauncher launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        mUri = uri;
                        ll_cut.setVisibility(View.VISIBLE);
                        playVideo(uri); // 播放视频
                    }
                });
        findViewById(R.id.btn_open).setOnClickListener(v -> launcher.launch("video/*"));
        findViewById(R.id.btn_cut_one).setOnClickListener(v -> {
            Bitmap bitmap = MediaUtil.getOneFrame(this, mUri, vv_content.getCurrentPosition());
            iv_frame.setImageBitmap(bitmap);
        });
        findViewById(R.id.btn_cut_multi).setOnClickListener(v -> {
            List<String> frameList = MediaUtil.getFrameList(this, mUri,
                    vv_content.getCurrentPosition(), 9);
            Intent intent = new Intent(this, PhotoGridActivity.class);
            intent.putStringArrayListExtra("path_list", (ArrayList<String>) frameList);
            startActivity(intent);
        });
    }

    private void playVideo(Uri uri) {
        vv_content.setVideoURI(uri); // 设置视频视图的视频路径
        MediaController mc = new MediaController(this); // 创建一个媒体控制条
        vv_content.setMediaController(mc); // 给视频视图设置相关联的媒体控制条
        mc.setMediaPlayer(vv_content); // 给媒体控制条设置相关联的视频视图
        vv_content.start(); // 视频视图开始播放
    }

    private int mLastPosition = 0;
    @Override
    protected void onResume() {
        super.onResume();
        // 恢复页面时立即从上次断点开始播放视频
        if (mLastPosition>0) {
            vv_content.seekTo(mLastPosition);
        }
        vv_content.start(); // 播放器开始播放
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLastPosition = vv_content.getCurrentPosition();
        vv_content.pause(); // 暂停播放
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vv_content.suspend(); // 释放播放资源
    }

}