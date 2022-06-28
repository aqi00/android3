package com.example.chapter19;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.example.chapter19.util.DateUtil;
import com.example.hmsml.face.camera.CameraConfiguration;
import com.example.hmsml.face.camera.LensEngine;
import com.example.hmsml.face.camera.LensEnginePreview;
import com.example.hmsml.face.transactor.LocalFaceTransactor;
import com.example.hmsml.face.views.overlay.GraphicOverlay;

public class FaceDetectActivity extends AppCompatActivity {
    private static final String TAG = "FaceDetectActivity";
    private LensEngine mLensEngine; // 声明一个透镜引擎对象
    private LensEnginePreview lep_preview; // 声明一个透镜引擎视图对象
    private GraphicOverlay go_overlay; // 声明一个图形覆盖板对象
    private int mCameraType = CameraConfiguration.CAMERA_FACING_BACK; // 默认前置摄像头
    private LocalFaceTransactor mTransactor; // 声明一个本地人脸办理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detect);
        findViewById(R.id.ib_back).setOnClickListener(v -> finish());
        findViewById(R.id.iv_switch).setOnClickListener(v -> {
            mCameraType = mCameraType==CameraConfiguration.CAMERA_FACING_BACK ?
                    CameraConfiguration.CAMERA_FACING_FRONT : CameraConfiguration.CAMERA_FACING_BACK;
            createLensEngine(); // 创建透镜引擎
            lep_preview.stop(); // 透镜引擎视图停止工作
            restartLensEngine(); // 重新启动透镜引擎
        });
        lep_preview = findViewById(R.id.lep_preview);
        go_overlay = findViewById(R.id.go_overlay);
        createLensEngine(); // 创建透镜引擎
        findViewById(R.id.btn_save).setOnClickListener(v -> saveFace());
    }

    // 保存人脸图片
    private void saveFace() {
        String path = String.format("%s/%s.jpg",
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), DateUtil.getNowDateTime());
        Log.d(TAG, "path="+path);
        mTransactor.saveFace(path); // 人脸办理器对象保存人脸
        //BitmapUtil.notifyPhotoAlbum(this, path);
    }

    // 创建透镜引擎
    private void createLensEngine() {
        CameraConfiguration configuration = new CameraConfiguration();
        configuration.setCameraFacing(mCameraType); // 设置摄像头朝向
        if (mLensEngine == null) {
            mLensEngine = new LensEngine(this, configuration, go_overlay);
        }
        mTransactor = new LocalFaceTransactor(this);
        // 给透镜引擎设置机器学习的框架办理器
        mLensEngine.setMachineLearningFrameTransactor(mTransactor);
    }

    // 重新启动透镜引擎
    private void restartLensEngine() {
        startLensEngine(); // 启动透镜引擎
        if (null != mLensEngine) {
            // 获取透镜引擎使用的摄像头对象
            Camera camera = mLensEngine.getCamera();
            try {
                // 把透镜引擎视图的表面纹理设置为相机的预览纹理
                camera.setPreviewTexture(lep_preview.getSurfaceTexture());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 启动透镜引擎
    private void startLensEngine() {
        if (mLensEngine != null) {
            try { // 透镜引擎视图启动透镜引擎
                lep_preview.start(mLensEngine, true);
            } catch (Exception e) {
                e.printStackTrace();
                mLensEngine.release(); // 释放透镜引擎
                mLensEngine = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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