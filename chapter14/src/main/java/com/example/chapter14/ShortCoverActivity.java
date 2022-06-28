package com.example.chapter14;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chapter14.adapter.CoverRecyclerAdapter;
import com.example.chapter14.util.MediaUtil;

import java.util.List;

public class ShortCoverActivity extends AppCompatActivity {
    private final static String TAG = "ShortCoverActivity";
    private VideoView vv_content; // 声明一个视频视图对象
    private RecyclerView rv_cover; // 声明一个循环视图对象
    private int mCoverPos; // 封面图片的序号
    private String mVideoPath; // 视频文件路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_cover);
        initView(); // 初始化视图
        playVideo(); // 播放视频
    }

    // 初始化视图
    private void initView() {
        vv_content = findViewById(R.id.vv_content);
        rv_cover = findViewById(R.id.rv_cover);
        // 创建一个水平方向的线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        rv_cover.setLayoutManager(manager); // 设置循环视图的布局管理器
        rv_cover.setItemAnimator(new DefaultItemAnimator()); // 设置循环视图的动画效果
        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
        findViewById(R.id.btn_save).setOnClickListener(v -> saveCover());
    }

    // 播放视频
    private void playVideo() {
        mCoverPos = getIntent().getIntExtra("cover_pos", 0);
        mVideoPath = getIntent().getStringExtra("video_path");
        vv_content.setVideoURI(Uri.parse(mVideoPath)); // 设置视频视图的视频路径
        // 设置视频视图的就绪监听器，在播放源准备完成后，命令媒体播放器循环播放视频
        vv_content.setOnPreparedListener(mp -> mp.setLooping(true));
        vv_content.start(); // 视频视图开始播放
        new Thread(() -> renderCoverList()).start(); // 启动线程渲染视频封面的图片列表
    }

    // 渲染视频封面的图片列表
    private void renderCoverList() {
        // 获取视频文件中的图片帧列表
        List<String> pathList = MediaUtil.getFrameList(this, Uri.parse(mVideoPath), 0, 15);
        // 创建封面图片的循环适配器
        CoverRecyclerAdapter adapter = new CoverRecyclerAdapter(
                this, mCoverPos, pathList, position -> mCoverPos=position);
        runOnUiThread(() -> rv_cover.setAdapter(adapter)); // 回到主线程展示图片列表
    }

    // 保存视频封面
    private void saveCover() {
        Intent intent = new Intent(); // 创建一个新意图
        intent.putExtra("cover_pos", mCoverPos);
        setResult(Activity.RESULT_OK, intent); // 携带意图返回前一个页面
        finish(); // 关闭当前页面
    }

    private int mCurrentPosition = 0; // 当前的播放位置
    @Override
    protected void onResume() {
        super.onResume();
        // 恢复页面时立即从上次断点开始播放视频
        if (mCurrentPosition>0 && !vv_content.isPlaying()) {
            vv_content.seekTo(mCurrentPosition); // 找到指定位置
            vv_content.start(); // 视频视图开始播放
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停页面时保存当前的播放进度
        if (vv_content.isPlaying()) { // 视频视图正在播放
            // 获得视频视图当前的播放位置
            mCurrentPosition = vv_content.getCurrentPosition();
            vv_content.pause(); // 视频视图暂停播放
        }
    }

}