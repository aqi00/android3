package com.example.chapter14;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter14.util.MediaUtil;
import com.example.chapter14.widget.ArcView;
import com.example.chapter14.widget.CameraXView;

public class ShortTakeActivity extends AppCompatActivity {
    private final static String TAG = "ShortTakeActivity";
    private RelativeLayout rl_preview; // 声明一个相对布局对象
    private VideoView vv_content; // 声明一个视频视图对象
    private RelativeLayout rl_record; // 声明一个相对布局对象
    private CameraXView cxv_preview; // 声明一个二代相机视图对象
    private TextView tv_cost; // 声明一个文本视图对象
    private ImageView iv_record; // 声明一个图像视图对象
    private ArcView av_progress; // 声明一个圆弧视图对象
    private LinearLayout ll_next; // 声明一个线性布局对象
    private boolean isRecording = false; // 是否正在录像
    private int MAX_RECORD_TIME = 15; // 最大录制时长，默认15秒
    private String mVideoPath; // 待上传的视频文件路径
    private int CHOOSE_CODE = 3; // 只在视频库挑选视频的请求码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_take);
        initView(); // 初始化视图
        initCamera(); // 初始化相机
    }

    // 初始化视图
    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        rl_preview = findViewById(R.id.rl_preview);
        cxv_preview = findViewById(R.id.cxv_preview);
        vv_content = findViewById(R.id.vv_content);
        rl_record = findViewById(R.id.rl_record);
        tv_cost = findViewById(R.id.tv_cost);
        iv_record = findViewById(R.id.iv_record);
        av_progress = findViewById(R.id.av_progress);
        iv_record.setOnClickListener((v) -> dealRecord()); // 处理录像动作
        // 注册一个善后工作的活动结果启动器，获取指定类型的内容
        ActivityResultLauncher launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        // 把指定Uri的视频复制一份到内部存储空间，并返回存储路径
                        mVideoPath = MediaUtil.copyVideoFromUri(this, uri);
                        playVideo(); // 播放视频
                    }
                });
        findViewById(R.id.btn_album).setOnClickListener(v -> launcher.launch("video/*"));
        findViewById(R.id.iv_switch).setOnClickListener((v) -> cxv_preview.switchCamera());
        ll_next = findViewById(R.id.ll_next);
        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
        findViewById(R.id.btn_next).setOnClickListener((v) -> {
            // 下面跳到视频编辑界面
            Intent intent = new Intent(this, ShortEditActivity.class);
            intent.putExtra("video_path", mVideoPath);
            startActivity(intent);
        });
    }

    // 处理录像动作
    private void dealRecord() {
        if (!isRecording) {
            iv_record.setImageResource(R.drawable.short_stop);
            cxv_preview.startRecord(MAX_RECORD_TIME); // 开始录像
            startRecordAnim(); // 播放录像动画
            isRecording = !isRecording;
        } else {
            rl_record.setVisibility(View.GONE);
            cxv_preview.stopRecord(); // 停止录像
        }
    }

    // 初始化相机
    private void initCamera() {
        // 初始化二代相机，并指定准备就绪监听器和停止录像监听器
        cxv_preview.openCamera(this, CameraXView.MODE_RECORD, (result) -> {
            runOnUiThread(() -> {
                isRecording = false;
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                mVideoPath = cxv_preview.getVideoPath();
                playVideo(); // 播放视频
            });
        });
    }

    // 播放视频
    private void playVideo() {
        cxv_preview.closeCamera(); // 关闭相机
        rl_preview.setVisibility(View.GONE);
        rl_record.setVisibility(View.GONE);
        vv_content.setVisibility(View.VISIBLE);
        vv_content.setVideoURI(Uri.parse(mVideoPath)); // 设置视频视图的视频路径
        // 设置视频视图的就绪监听器，在播放源准备完成后，命令媒体播放器循环播放视频
        vv_content.setOnPreparedListener(mp -> mp.setLooping(true));
        vv_content.start(); // 视频视图开始播放
        ll_next.setVisibility(View.VISIBLE);
    }

    // 播放录像动画
    private void startRecordAnim() {
        tv_cost.setVisibility(View.VISIBLE);
        av_progress.setVisibility(View.VISIBLE);
        // 定义一个圆弧渐进动画
        Animation animation = new Animation() {
            private String costDesc=""; // 耗时描述
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                String cost = String.format("%.1f秒", MAX_RECORD_TIME*interpolatedTime);
                if (!costDesc.equals(cost)) { // 秒数发生变化
                    costDesc = cost;
                    tv_cost.setText(costDesc);
                    av_progress.setAngle((int) (360*interpolatedTime)); // 设置圆弧的角度
                }
            }
        };
        animation.setDuration(MAX_RECORD_TIME*1000); // 设置动画的持续时间
        tv_cost.startAnimation(animation); // 开始播放动画
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cxv_preview.closeCamera(); // 关闭相机
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//        if (resultCode==RESULT_OK && requestCode==CHOOSE_CODE) { // 从视频库回来
//            if (intent.getData() != null) { // 选择一个视频
//                Uri uri = intent.getData(); // 获得已选择视频的路径对象
//                // 把指定Uri的视频复制一份到内部存储空间，并返回存储路径
//                mVideoPath = MediaUtil.copyVideoFromUri(this, uri);
//                playVideo(); // 播放视频
//            }
//        }
//    }

    private int mCurrentPosition = 0; // 当前的播放位置
    @Override
    protected void onResume() {
        super.onResume();
        // 恢复页面时立即从上次断点开始播放视频
        if (!TextUtils.isEmpty(mVideoPath) && mCurrentPosition>0 && !vv_content.isPlaying()) {
            vv_content.seekTo(mCurrentPosition); // 找到指定位置
            vv_content.start(); // 视频视图开始播放
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停页面时保存当前的播放进度
        if (!TextUtils.isEmpty(mVideoPath) && vv_content.isPlaying()) { // 视频视图正在播放
            // 获得视频视图当前的播放位置
            mCurrentPosition = vv_content.getCurrentPosition();
            vv_content.pause(); // 视频视图暂停播放
        }
    }

}