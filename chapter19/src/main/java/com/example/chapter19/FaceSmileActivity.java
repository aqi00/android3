package com.example.chapter19;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.hmsml.image.util.ImageUtils;
import com.example.hmsml.smile.camera.LensEnginePreview;
import com.example.hmsml.smile.views.overlay.GraphicOverlay;
import com.example.hmsml.smile.views.graphic.LocalFaceGraphic;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.LensEngine;
import com.huawei.hms.mlsdk.common.MLAnalyzer;
import com.huawei.hms.mlsdk.common.MLResultTrailer;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;
import com.huawei.hms.mlsdk.face.MLFaceEmotion;
import com.huawei.hms.mlsdk.face.MLMaxSizeFaceTransactor;

public class FaceSmileActivity extends AppCompatActivity {
    private static final String TAG = "FaceSmileActivity";
    private MLFaceAnalyzer mAnalyzer; // 声明一个人脸分析器对象
    private LensEngine mLensEngine; // 声明一个透镜引擎对象
    private LensEnginePreview lep_preview; // 声明一个透镜引擎视图对象
    private GraphicOverlay go_overlay; // 声明一个图形覆盖板对象
    private int mLensType = LensEngine.BACK_LENS; // 默认后置摄像头
    private final float mSmilingPossibility = 0.95f; // 为笑脸的可能性
    private boolean isSafeToTake = false; // 是否能够安全拍照
    private ImageView iv_restart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_smile);
        lep_preview = findViewById(R.id.lep_preview);
        go_overlay = findViewById(R.id.go_overlay);
        iv_restart = findViewById(R.id.iv_restart);
        findViewById(R.id.ib_back).setOnClickListener(v -> finish());
        iv_restart.setOnClickListener(v -> startPreview());
        findViewById(R.id.iv_switch).setOnClickListener(v -> {
            mLensType = mLensType==LensEngine.BACK_LENS ? LensEngine.FRONT_LENS : LensEngine.BACK_LENS;
            if (mLensEngine != null) {
                mLensEngine.close(); // 关闭透镜引擎
            }
            startPreview(); // 启动预览画面
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPreview(); // 启动预览画面
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
            mLensEngine.release(); // 释放透镜引擎
        }
        if (mAnalyzer != null) {
            try {
                mAnalyzer.stop(); // 人脸分析器停止工作
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 启动透镜引擎
    private void startLensEngine() {
        iv_restart.setVisibility(View.GONE);
        if (mLensEngine != null) {
            try { // 透镜引擎视图启动透镜引擎
                lep_preview.start(mLensEngine, go_overlay);
                isSafeToTake = true;
            } catch (Exception e) {
                e.printStackTrace();
                mLensEngine.release(); // 释放透镜引擎
                mLensEngine = null;
            }
        }
    }

    // 启动预览画面
    public void startPreview() {
        createFaceAnalyzer(); // 创建人脸分析器
        lep_preview.release(); // 透镜引擎视图释放资源
        // 创建指定参数的透镜引擎
        mLensEngine = new LensEngine.Creator(this, mAnalyzer)
                .setLensType(mLensType) // 设置透镜类型
                .applyDisplayDimension(640, 480) // 显示的宽高尺寸
                .applyFps(25.0f) // 每秒传输帧数
                .enableAutomaticFocus(true) // 启用自动对焦
                .create();
        startLensEngine(); // 启动透镜引擎
    }

    // 创建人脸分析器
    private void createFaceAnalyzer() {
        MLFaceAnalyzerSetting setting = new MLFaceAnalyzerSetting.Factory()
                        .setFeatureType(MLFaceAnalyzerSetting.TYPE_FEATURES)
                        .setKeyPointType(MLFaceAnalyzerSetting.TYPE_UNSUPPORT_KEYPOINTS)
                        .setMinFaceProportion(0.1f) // 设置人脸在景象中的最小比例
                        .setTracingAllowed(true).create();
        mAnalyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer(setting);
        // 创建最大尺寸的人脸办理器，用于发现人脸后的即时处理
        MLMaxSizeFaceTransactor transactor = new MLMaxSizeFaceTransactor.Creator(mAnalyzer, new MLResultTrailer<MLFace>() {
            @Override
            public void objectCreateCallback(int itemId, MLFace obj) {
                captureSmile(obj, true); // 捕捉笑脸动作
            }

            @Override
            public void objectUpdateCallback(MLAnalyzer.Result<MLFace> var1, MLFace obj) {
                captureSmile(obj, isSafeToTake); // 捕捉笑脸动作
            }

            @Override
            public void lostCallback(MLAnalyzer.Result<MLFace> result) {
                go_overlay.clear(); // 清空图形覆盖板
            }

            @Override
            public void completeCallback() {
                go_overlay.clear(); // 清空图形覆盖板
            }
        }).create();
        mAnalyzer.setTransactor(transactor); // 给人脸分析器设置人脸办理器
    }

    // 捕捉笑脸动作
    private void captureSmile(MLFace obj, boolean isSafe) {
        go_overlay.clear(); // 清空图形覆盖板
        if (obj == null) {
            return;
        }
        LocalFaceGraphic faceGraphic = new LocalFaceGraphic(go_overlay, obj, this);
        go_overlay.addGraphic(faceGraphic); // 把人脸图画添加至图形覆盖板
        MLFaceEmotion emotion = obj.getEmotions(); // 获取人脸情感
        if (emotion.getSmilingProbability() > mSmilingPossibility && isSafe) {
            isSafeToTake = false;
            // 发现捕捉到笑脸，于是立即拍照并保存到相册
            mLensEngine.photograph(null, bytes -> {
                        stopPreview(); // 停止预览画面
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        new ImageUtils(this).saveToAlbum(bitmap);
            });
        }
    }

    // 停止预览画面
    private void stopPreview() {
        iv_restart.setVisibility(View.VISIBLE);
        if (mLensEngine != null) {
            mLensEngine.release(); // 释放透镜引擎
            isSafeToTake = false;
        }
        if (mAnalyzer != null) {
            try {
                mAnalyzer.stop(); // 人脸分析器停止工作
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}