package com.example.chapter19;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chapter19.util.BitmapUtil;
import com.example.hmsml.face.util.BitmapUtils;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.faceverify.MLFaceTemplateResult;
import com.huawei.hms.mlsdk.faceverify.MLFaceVerificationAnalyzer;
import com.huawei.hms.mlsdk.faceverify.MLFaceVerificationAnalyzerFactory;
import com.huawei.hms.mlsdk.faceverify.MLFaceVerificationAnalyzerSetting;
import com.huawei.hms.mlsdk.faceverify.MLFaceVerificationResult;

import java.util.ArrayList;
import java.util.List;

public class FaceVerifyActivity extends AppCompatActivity {
    private static final String TAG = "FaceVerifyActivity";
    private static final int FACEMAX = 3; // 最多识别的人脸数量
    private TextView tv_result;
    private ImageView iv_first;
    private ImageView iv_second;
    private Bitmap mFirstBitmap, mFirstBitmapCopy; // 第一张人脸位图及其副本
    private Bitmap mSecondBitmap, mSecondBitmapCopy; // 第二张人脸位图及其副本
    private MLFaceVerificationAnalyzer mAnalyzer; // 声明一个人脸比对分析器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_verify);
        findViewById(R.id.ib_back).setOnClickListener(v -> finish());
        iv_first = findViewById(R.id.iv_first);
        iv_second = findViewById(R.id.iv_second);
        findViewById(R.id.btn_verify).setOnClickListener(v -> compareFace());
        tv_result = findViewById(R.id.tv_result);
        // 注册一个善后工作的活动结果启动器，获取指定类型的内容
        ActivityResultLauncher firstLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> choosePic(uri, true));
        findViewById(R.id.rl_first).setOnClickListener(v -> firstLauncher.launch("image/*"));
        // 注册一个善后工作的活动结果启动器，获取指定类型的内容
        ActivityResultLauncher secondLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(), uri -> choosePic(uri, false));
        findViewById(R.id.rl_second).setOnClickListener(v -> secondLauncher.launch("image/*"));
        initAnalyzer(); // 初始化人脸比对分析器
    }

    // 初始化人脸比对分析器
    private void initAnalyzer() {
        MLFaceVerificationAnalyzerSetting setting = new MLFaceVerificationAnalyzerSetting.Factory()
                .setMaxFaceDetected(FACEMAX).create();
        mAnalyzer = MLFaceVerificationAnalyzerFactory.getInstance().getFaceVerificationAnalyzer(setting);
    }

    // 挑选图片
    private void choosePic(Uri uri, boolean isFirst) {
        if (uri == null) {
            Toast.makeText(this, "请选择一张图片", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isFirst) { // 第一张图片
            mFirstBitmap = loadFacePic(uri, iv_first); // 加载人脸图片
            mFirstBitmapCopy = mFirstBitmap.copy(Bitmap.Config.ARGB_8888, true);
        } else { // 第二张图片
            mSecondBitmap = loadFacePic(uri, iv_second); // 加载人脸图片
            mSecondBitmapCopy = mSecondBitmap.copy(Bitmap.Config.ARGB_8888, true);
        }
    }

    // 加载人脸图片
    private Bitmap loadFacePic(Uri picUri, ImageView view) {
        Bitmap pic = BitmapUtils.loadFromPath(this, BitmapUtil.getPureUri(picUri),
                ((View) view.getParent()).getWidth(), ((View) view.getParent()).getHeight())
                .copy(Bitmap.Config.ARGB_8888, true);
        view.setImageBitmap(pic);
        view.setVisibility(View.VISIBLE);
        return pic;
    }

    // 比较两张人脸图片的相似度
    private void compareFace() {
        if (mFirstBitmap==null || mSecondBitmap==null) {
            Toast.makeText(this, "请先选择两张人脸图片", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // 给人脸比对分析器设置待比较的人脸模板
            List<MLFaceTemplateResult> results = mAnalyzer.setTemplateFace(MLFrame.fromBitmap(mFirstBitmap));
            for (MLFaceTemplateResult template : results) {
                // 在位图上面标出人脸相框
                BitmapUtil.drawFaceFrame(template.getFaceInfo().getFaceRect(), mFirstBitmapCopy);
                iv_first.setImageBitmap(mFirstBitmapCopy);
            }
            // 给人脸比对分析器创建异步分析任务，准备分析比较指定人脸
            Task<List<MLFaceVerificationResult>> task = mAnalyzer.asyncAnalyseFrame(MLFrame.fromBitmap(mSecondBitmap));
            // 给异步分析任务添加成功监听器和失败监听器
            task.addOnSuccessListener(compareList -> {
                List<Float> similarityList = new ArrayList<>();
                for (MLFaceVerificationResult template : compareList) {
                    // 在位图上面标出人脸相框
                    BitmapUtil.drawFaceFrame(template.getFaceInfo().getFaceRect(), mSecondBitmapCopy);
                    iv_second.setImageBitmap(mSecondBitmapCopy);
                    similarityList.add(template.getSimilarity()); // 添加人脸相似度
                }
                tv_result.setText("人脸相似度为："+similarityList.toString());
            }).addOnFailureListener(e -> tv_result.setText("人脸比对失败："+e.toString()));
        } catch (Exception e) {
            e.printStackTrace();
            tv_result.setText("人脸比对失败："+e.getMessage());
        }
    }

}