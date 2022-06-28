package com.example.chapter19;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chapter19.util.BitmapUtil;
import com.example.hmsml.image.util.BitmapUtils;
import com.example.hmsml.image.util.ImageUtils;
import com.example.hmsml.image.views.overlay.GraphicOverlay;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentation;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationAnalyzer;
import com.huawei.hms.mlsdk.imgseg.MLImageSegmentationSetting;

public class FacePickActivity extends AppCompatActivity {
    private static final String TAG = "FacePickActivity";
    private ImageView iv_origin;
    private ImageView iv_pick;
    private TextView tv_result;
    private Bitmap mOriginBitmap, mProcessedBitmap; // 原始位图和抠好的位图
    private Bitmap mForegroundBitmap, mBackgroundBitmap; // 前景位图和背景位图
    private GraphicOverlay go_overlay; // 声明一个图形覆盖板对象
    private MLImageSegmentationAnalyzer mAnalyzer; // 声明一个图像切片分析器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_pick);
        findViewById(R.id.ib_back).setOnClickListener(v -> finish());
        iv_origin = findViewById(R.id.iv_origin);
        iv_pick = findViewById(R.id.iv_pick);
        // 注册一个善后工作的活动结果启动器，获取指定类型的内容
        ActivityResultLauncher originLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> chooseOrigin(uri));
        findViewById(R.id.rl_origin).setOnClickListener(v -> originLauncher.launch("image/*"));
        // 注册一个善后工作的活动结果启动器，获取指定类型的内容
        ActivityResultLauncher pickLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> chooseBackground(uri));
        findViewById(R.id.rl_pick).setOnClickListener(v -> pickLauncher.launch("image/*"));
        findViewById(R.id.btn_save).setOnClickListener(v -> saveImage());
        tv_result = findViewById(R.id.tv_result);
        go_overlay = findViewById(R.id.go_overlay);
        initAnalyzer(); // 初始化图像切片分析器
    }

    // 初始化图像切片分析器
    private void initAnalyzer() {
        MLImageSegmentationSetting setting = new MLImageSegmentationSetting.Factory()
                .setAnalyzerType(MLImageSegmentationSetting.BODY_SEG).create();
        mAnalyzer = MLAnalyzerFactory.getInstance().getImageSegmentationAnalyzer(setting);
    }

    // 选择原始的人像图片
    private void chooseOrigin(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, "请选择一张人像图片", Toast.LENGTH_SHORT).show();
            return;
        }
        go_overlay.clear(); // 清空图形覆盖板
        mOriginBitmap = BitmapUtils.loadFromPath(this, BitmapUtil.getPureUri(uri),
                ((View) iv_origin.getParent()).getWidth(),
                ((View) iv_origin.getParent()).getHeight());
        Log.d(TAG, "resized image size width:" + mOriginBitmap.getWidth() + ",height: " + mOriginBitmap.getHeight());
        iv_origin.setImageBitmap(mOriginBitmap);
        createImageTask(); // 创建图像分析任务
        changeBackground(); // 变更背景画面
    }

    // 选择背景图片
    private void chooseBackground(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, "请选择一张背景图片", Toast.LENGTH_SHORT).show();
            return;
        }
        mBackgroundBitmap = BitmapUtils.loadFromPath(this, BitmapUtil.getPureUri(uri),
                ((View) iv_pick.getParent()).getWidth(),
                ((View) iv_pick.getParent()).getHeight());
        changeBackground(); // 变更背景画面
    }

    // 保存替换了背景的人像图片
    private void saveImage() {
        if (mProcessedBitmap == null) {
            Toast.makeText(this, "请先完成抠图操作", Toast.LENGTH_SHORT).show();
        } else {
            ImageUtils imageUtils = new ImageUtils(this);
            String filePath = imageUtils.saveToAlbum(mProcessedBitmap);
            tv_result.setText("已保存加工好的抠图人像："+filePath);
        }
    }

    // 变更背景画面
    private void changeBackground() {
        if (mForegroundBitmap!=null && mBackgroundBitmap!=null) {
            BitmapDrawable drawable = new BitmapDrawable(getResources(), mBackgroundBitmap);
            iv_pick.setDrawingCacheEnabled(true);
            iv_pick.setBackground(drawable);
            iv_pick.setImageBitmap(mForegroundBitmap);
            mProcessedBitmap = Bitmap.createBitmap(iv_pick.getDrawingCache());
            iv_pick.setDrawingCacheEnabled(false);
        }
    }

    // 创建图像分析任务
    private void createImageTask() {
        MLFrame mlFrame = new MLFrame.Creator().setBitmap(mOriginBitmap).create();
        // 给图像切片分析器创建异步分析任务，准备从图像中抠出人脸区域
        Task<MLImageSegmentation> task = mAnalyzer.asyncAnalyseFrame(mlFrame);
        // 给异步分析任务添加成功监听器和失败监听器
        task.addOnSuccessListener(results -> {
            if (results != null) {
                mForegroundBitmap = results.getForeground();
                iv_pick.setImageBitmap(mForegroundBitmap);
            } else {
                tv_result.setText("人像抠图未返回结果");
            }
        }).addOnFailureListener(e -> tv_result.setText("人像抠图失败："+e.getMessage()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mAnalyzer.stop(); // 图像切片分析器停止工作
            if (go_overlay != null) {
                go_overlay.clear(); // 清空图形覆盖板
                go_overlay = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}