package com.example.chapter19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.hmsml.text.camera.CameraConfiguration;
import com.example.hmsml.text.camera.LensEngine;
import com.example.hmsml.text.camera.LensEnginePreview;
import com.example.hmsml.text.transactor.LocalTextTransactor;
import com.example.hmsml.text.views.overlay.GraphicOverlay;

import com.huawei.hms.hmsscankit.ScanUtil;

public class RecognizeTextActivity extends AppCompatActivity {
    private final static String TAG = "RecognizeTextActivity";
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private boolean isInitialized = false; // 是否初始化
    private LensEngine mLensEngine; // 声明一个透镜引擎对象
    private LensEnginePreview lep_preview; // 声明一个透镜引擎视图对象
    private GraphicOverlay go_overlay; // 声明一个图形覆盖板对象
    private LocalTextTransactor mLocalTransactor; // 声明一个本地文本办理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize_text);
        lep_preview = findViewById(R.id.lep_preview);
        go_overlay = findViewById(R.id.go_overlay);
        findViewById(R.id.ib_back).setOnClickListener(v -> finish());
        findViewById(R.id.ib_take).setOnClickListener(v -> takeResult());
    }

    // 创建透镜引擎
    private void createLensEngine() {
        if (mLensEngine == null) {
            mLensEngine = new LensEngine(this, new CameraConfiguration(), go_overlay);
        }
        mLocalTransactor = new LocalTextTransactor(mHandler, this);
        // 给透镜引擎设置机器学习的框架办理器
        mLensEngine.setMachineLearningFrameTransactor(mLocalTransactor);
        isInitialized = true;
    }

    // 启动透镜引擎
    private void startLensEngine() {
        if (mLensEngine != null) {
            try { // 透镜引擎视图启动透镜引擎
                lep_preview.start(mLensEngine, false);
            } catch (Exception e) {
                e.printStackTrace();
                mLensEngine.release(); // 释放透镜引擎
                mLensEngine = null;
            }
        }
    }

    // 获取文本识别结果
    private void takeResult() {
        String recognizeResult = mLocalTransactor.getTextResult();
        Log.d(TAG, "识别文本为："+recognizeResult);
        Intent intent = new Intent(this, RecognizeResultActivity.class);
        intent.putExtra(ScanUtil.RESULT, recognizeResult);
        startActivity(intent); // 跳到识别结果页
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isInitialized) { // 尚未初始化
            createLensEngine(); // 创建透镜引擎
        }
        startLensEngine(); // 启动透镜引擎
    }

    @Override
    protected void onPause() {
        super.onPause();
        lep_preview.stop(); // 透镜引擎视图停止工作
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLensEngine != null) {
            try{
                mLensEngine.release(); // 释放透镜引擎
            } catch (Exception e) {
                e.printStackTrace();
            }
            mLensEngine = null;
        }
    }

}