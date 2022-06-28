package com.example.chapter14;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter14.widget.CameraXView;

public class CameraxRecordActivity extends AppCompatActivity {
    private final static String TAG = "CameraxRecordActivity";
    private CameraXView cxv_preview; // 声明一个增强相机视图对象
    private Chronometer chr_cost; // 声明一个计时器对象
    private ImageView iv_record; // 声明一个图像视图对象
    private boolean isRecording = false; // 是否正在录像

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax_record);
        initView(); // 初始化视图
        initCamera(); // 初始化相机
    }

    // 初始化视图
    private void initView() {
        cxv_preview = findViewById(R.id.cxv_preview);
        chr_cost = findViewById(R.id.chr_cost);
        iv_record = findViewById(R.id.iv_record);
        iv_record.setOnClickListener((v) -> dealRecord()); // 处理录像动作
        findViewById(R.id.iv_switch).setOnClickListener((v) -> cxv_preview.switchCamera());
        findViewById(R.id.btn_play).setOnClickListener((v) -> {
            if (TextUtils.isEmpty(cxv_preview.getVideoPath())) {
                Toast.makeText(this, "请先录像再观看视频", Toast.LENGTH_SHORT).show();
                return;
            }
            // 下面跳到视频播放界面
            Intent intent = new Intent(this, VideoDetailActivity.class);
            intent.putExtra("video_path", cxv_preview.getVideoPath());
            startActivity(intent);
        });
    }

    // 初始化相机
    private void initCamera() {
        // 打开增强相机，并指定停止录像监听器
        cxv_preview.openCamera(this, CameraXView.MODE_RECORD, (result) -> {
            runOnUiThread(() -> {
                chr_cost.setVisibility(View.GONE);
                chr_cost.stop(); // 停止计时
                iv_record.setImageResource(R.drawable.record_start);
                iv_record.setEnabled(true);
                isRecording = false;
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            });
        });
    }

    // 处理录像动作
    private void dealRecord() {
        if (!isRecording) {
            iv_record.setImageResource(R.drawable.record_stop);
            cxv_preview.startRecord(15); // 开始录像
            chr_cost.setVisibility(View.VISIBLE);
            chr_cost.setBase(SystemClock.elapsedRealtime()); // 设置计时器的基准时间
            chr_cost.start(); // 开始计时
            isRecording = !isRecording;
        } else {
            iv_record.setEnabled(false);
            cxv_preview.stopRecord(); // 停止录像
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cxv_preview.closeCamera(); // 关闭相机
    }

}