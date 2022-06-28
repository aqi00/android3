package com.example.chapter14;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        VideoView vv_content = findViewById(R.id.vv_content);
        String video_path = getIntent().getStringExtra("video_path");
        vv_content.setVideoURI(Uri.parse("file://" + video_path)); // 设置视频视图的视频路径
        MediaController mc = new MediaController(this); // 创建一个媒体控制条
        vv_content.setMediaController(mc); // 给视频视图设置相关联的媒体控制条
        mc.setMediaPlayer(vv_content); // 给媒体控制条设置相关联的视频视图
        vv_content.start(); // 视频视图开始播放
    }
}