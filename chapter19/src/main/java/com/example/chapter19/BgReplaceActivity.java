package com.example.chapter19;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.chapter19.util.BitmapUtil;
import com.example.chapter19.util.Utils;
import com.example.hmsml.face.util.BitmapUtils;
import com.example.hmsml.image.camera.CameraConfiguration;
import com.example.hmsml.image.camera.LensEngine;
import com.example.hmsml.image.camera.LensEnginePreview;
import com.example.hmsml.image.transactor.ImageSegmentationTransactor;
import com.example.hmsml.image.util.ImageUtils;
import com.example.hmsml.image.views.overlay.GraphicOverlay;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationScene;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting;

public class BgReplaceActivity extends AppCompatActivity {
    private final static String TAG = "BgReplaceActivity";
    private LensEngine mLensEngine; // 声明一个透镜引擎对象
    private LensEnginePreview lep_preview; // 声明一个透镜引擎视图对象
    private GraphicOverlay go_overlay; // 声明一个图形覆盖板对象
    private ImageView iv_look;
    private int mCameraType = CameraConfiguration.CAMERA_FACING_FRONT; // 默认前置摄像头
    private Bitmap mBackground, mProcessImage; // 背景位图，处理后的位图
    private String mImagePath; // 图片文件路径
    private RenderScript mRenderScript; // 声明一个渲染脚本对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bg_replace);
        mBackground = Bitmap.createBitmap(Utils.getScreenWidth(this),
                Utils.getScreenHeight(this), Bitmap.Config.ARGB_8888);
        initView(); // 初始化视图
        createLensEngine(); // 创建透镜引擎
        mRenderScript = RenderScript.create(this); // 创建渲染脚本
    }

    // 初始化视图
    private void initView() {
        lep_preview = findViewById(R.id.lep_preview);
        go_overlay = findViewById(R.id.go_overlay);
        iv_look = findViewById(R.id.iv_look);
        findViewById(R.id.ib_back).setOnClickListener(v -> finish());
        findViewById(R.id.iv_switch).setOnClickListener(v -> switchCamera());
        // 注册一个善后工作的活动结果启动器，获取指定类型的内容
        ActivityResultLauncher launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> chooseBackground(uri));
        findViewById(R.id.iv_bg).setOnClickListener(v -> launcher.launch("image/*"));
        findViewById(R.id.iv_take).setOnClickListener(v -> takeProcessPhoto());
        iv_look.setOnClickListener(v -> {
            if (mImagePath == null) {
                Toast.makeText(this, "请先拍摄照片", Toast.LENGTH_SHORT).show();
            } else {
                BitmapUtil.viewAlbum(this, mImagePath);
            }
        });
    }

    // 切换摄像头
    private void switchCamera() {
        mCameraType = mCameraType== CameraConfiguration.CAMERA_FACING_BACK ?
                CameraConfiguration.CAMERA_FACING_FRONT : CameraConfiguration.CAMERA_FACING_BACK;
        createLensEngine(); // 创建透镜引擎
        lep_preview.stop(); // 透镜引擎视图停止工作
        restartLensEngine(); // 重新启动透镜引擎
    }

    // 选择背景图片
    private void chooseBackground(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, "请选择一张背景图片", Toast.LENGTH_SHORT).show();
            return;
        }
        mBackground = BitmapUtils.loadFromPath(this, BitmapUtil.getPureUri(uri),
                ((View) lep_preview.getParent()).getWidth(),
                ((View) lep_preview.getParent()).getHeight())
                .copy(Bitmap.Config.ARGB_8888, true);
        createLensEngine(); // 创建透镜引擎
    }

    // 创建透镜引擎
    private void createLensEngine() {
        CameraConfiguration configuration = new CameraConfiguration();
        configuration.setCameraFacing(mCameraType); // 设置摄像头朝向
        if (mLensEngine == null) {
            mLensEngine = new LensEngine(this, configuration, go_overlay);
        }
        MLImageSegmentationSetting setting = new MLImageSegmentationSetting.Factory()
                .setAnalyzerType(MLImageSegmentationSetting.BODY_SEG)
                .setScene(MLImageSegmentationScene.FOREGROUND_ONLY)
                .setExact(false).create();
        // 创建图像切片办理器
        ImageSegmentationTransactor transactor = new ImageSegmentationTransactor(
                this, setting, mBackground);
        // 设置图像切片结果的回调监听器
        transactor.setImageSegmentationResultCallBack(bitmap -> mProcessImage = bitmap);
        // 给透镜引擎设置机器学习的框架办理器
        mLensEngine.setMachineLearningFrameTransactor(transactor);
    }

    // 启动透镜引擎
    private void startLensEngine() {
        try { // 透镜引擎视图启动透镜引擎
            lep_preview.start(mLensEngine, true);
        } catch (Exception e) {
            e.printStackTrace();
            mLensEngine.release(); // 释放透镜引擎
            mLensEngine = null;
            mImagePath = null;
        }
    }

    // 重新启动透镜引擎
    private void restartLensEngine() {
        if (null != mLensEngine) {
            startLensEngine(); // 启动透镜引擎
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

    // 拍摄替换了背景的照片
    private void takeProcessPhoto() {
        if (mProcessImage == null) {
            Toast.makeText(this, "未能拍到照片", Toast.LENGTH_SHORT).show();
        } else {
            ImageUtils imageUtils = new ImageUtils(this);
            imageUtils.setImageUtilCallBack(path -> mImagePath = path);
            imageUtils.saveToAlbum(mProcessImage); // 把图片保存到相册
            Bitmap resizedBitmap = BitmapUtil.getScaleBitmap(mProcessImage, 0.2);
            iv_look.setImageBitmap(resizedBitmap);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startLensEngine(); // 启动透镜引擎
    }

    @Override
    protected void onStop() {
        super.onStop();
        lep_preview.stop(); // 透镜引擎视图停止工作
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mLensEngine != null) {
                mLensEngine.release(); // 释放透镜引擎
                mLensEngine = null;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                RenderScript.releaseAllContexts();
            }else {
                mRenderScript.finish(); // 渲染脚本结束工作
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}