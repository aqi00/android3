package com.example.chapter14;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayActivity extends AppCompatActivity {
    private final static String TAG = "VideoPlayActivity";
    private VideoView vv_content; // 声明一个视频视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        // 从布局文件中获取名叫vv_content的视频视图
        vv_content = findViewById(R.id.vv_content);
        // 注册一个善后工作的活动结果启动器，获取指定类型的内容
        ActivityResultLauncher launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        playVideo(uri); // 播放视频
                    }
                });
        findViewById(R.id.btn_choose).setOnClickListener(v -> launcher.launch("video/*"));
    }

    private void playVideo(Uri uri) {
        vv_content.setVideoURI(uri); // 设置视频视图的视频路径
        MediaController mc = new MediaController(this); // 创建一个媒体控制条
        vv_content.setMediaController(mc); // 给视频视图设置相关联的媒体控制条
        mc.setMediaPlayer(vv_content); // 给媒体控制条设置相关联的视频视图
        vv_content.start(); // 视频视图开始播放
    }

}
