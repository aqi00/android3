package com.example.chapter19;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter19.bean.DetectedFace;
import com.example.chapter19.dao.PersonDao;
import com.example.chapter19.entity.PersonPortrait;
import com.example.chapter19.util.BitmapUtil;
import com.example.chapter19.util.DateUtil;
import com.example.hmsml.face.camera.CameraConfiguration;
import com.example.hmsml.face.camera.LensEngine;
import com.example.hmsml.face.camera.LensEnginePreview;
import com.example.hmsml.face.transactor.LocalFaceTransactor;
import com.example.hmsml.face.views.overlay.GraphicOverlay;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.faceverify.MLFaceTemplateResult;
import com.huawei.hms.mlsdk.faceverify.MLFaceVerificationAnalyzer;
import com.huawei.hms.mlsdk.faceverify.MLFaceVerificationAnalyzerFactory;
import com.huawei.hms.mlsdk.faceverify.MLFaceVerificationAnalyzerSetting;
import com.huawei.hms.mlsdk.faceverify.MLFaceVerificationResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PersonVerifyActivity extends AppCompatActivity {
    private final static String TAG = "PersonVerifyActivity";
    private TextView tv_option; // 声明一个文本视图对象
    private String mPersonName; // 人员名称
    private PersonDao personDao; // 声明一个人员的持久化对象
    private List<Bitmap> mSampleList = new ArrayList<>(); // 样本头像列表
    private List<DetectedFace> mDetectList = new ArrayList<>(); // 检测头像列表
    private Timer mTimer = new Timer(); // 创建一个定时器
    private VerifyTask mTimerTask; // 声明一个人脸识别的定时器任务对象

    private LensEngine mLensEngine; // 声明一个透镜引擎对象
    private LensEnginePreview lep_preview; // 声明一个透镜引擎视图对象
    private GraphicOverlay go_overlay; // 声明一个图形覆盖板对象
    private int mCameraType = CameraConfiguration.CAMERA_FACING_BACK; // 默认后置摄像头
    private LocalFaceTransactor mTransactor; // 声明一个本地人脸办理器对象
    private static final int FACEMAX = 3; // 最多识别的人脸数量
    private MLFaceVerificationAnalyzer mAnalyzer; // 声明一个人脸分析器对象
    private String mPath; // 人脸位图的临时保存路径
    private Bitmap mSecondBitmapCopy; // 找到的人脸位图副本

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_verify);
        mPersonName = getIntent().getStringExtra("person_name");
        // 从App实例中获取唯一的人员持久化对象
        personDao = MainApplication.getInstance().getPersonDB().personDao();
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("正在追踪"+mPersonName);
        tv_option = findViewById(R.id.tv_option);
        tv_option.setText("完成");
        tv_option.setOnClickListener(v -> finishVerify());
        new Thread(() -> initSample()).start(); // 启动样本初始化线程
        lep_preview = findViewById(R.id.lep_preview);
        go_overlay = findViewById(R.id.go_overlay);
        createLensEngine(); // 创建人脸识别引擎
        initAnalyzer(); // 初始化人脸分析器
    }

    // 创建人脸识别引擎
    private void createLensEngine() {
        mTransactor = new LocalFaceTransactor(this);
        CameraConfiguration configuration = new CameraConfiguration();
        configuration.setCameraFacing(mCameraType); // 设置摄像头朝向
        if (mLensEngine == null) {
            mLensEngine = new LensEngine(this, configuration, go_overlay);
            // 给透镜引擎设置机器学习的框架办理器
            mLensEngine.setMachineLearningFrameTransactor(mTransactor);
        }
    }

    // 启动人脸识别引擎
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
    protected void onResume() {
        super.onResume();
        mPath = String.format("%s/%s.jpg",
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), DateUtil.getNowDateTime());
        Log.d(TAG, "path="+mPath);
        mTransactor.saveFace(mPath); // 人脸办理器对象保存人脸
        mTimerTask = new VerifyTask();
        // 命令定时器启动定时任务。
        // 调度规则为：延迟200毫秒后启动，且之后每间隔1500毫秒再执行一个任务
        mTimer.scheduleAtFixedRate(mTimerTask, 20, 1500);
        startLensEngine(); // 启动人脸识别引擎
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTimer.cancel(); // 取消定时器
        mTimerTask.cancel(); // 取消定时器任务
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

    // 结束人脸识别
    private void finishVerify() {
        ArrayList<String> pathList = new ArrayList<>();
        for (DetectedFace detectFace : mDetectList) {
            String face_path = String.format("%s/%s.jpg",
                    getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),
                    DateUtil.getNowDateTimeDetail());
            BitmapUtil.saveImage(face_path, detectFace.getBitmap()); // 把位图保存为图片文件
            face_path = face_path + "|" + detectFace.getSimilarity();
            pathList.add(face_path);
        }
        Intent intent = new Intent(); // 创建一个新意图
        intent.putStringArrayListExtra("path_list", pathList); // 把快递包裹塞给意图
        setResult(Activity.RESULT_OK, intent); // 携带意图返回上一个页面
        finish(); // 关闭当前页面
    }

    // 初始化样本头像
    private void initSample() {
        // 从数据库中读取该人员的所有样本头像
        List<PersonPortrait> sampleList = personDao.queryPersonPortrait(mPersonName, 0);
        for (PersonPortrait portrait : sampleList) {
            Bitmap bitmap = BitmapFactory.decodeFile(portrait.getPath());
            mSampleList.add(bitmap);
        }
    }

    // 定义一个人脸识别线程，比较待检测人脸与样本人脸的相似度
    private class VerifyTask extends TimerTask {
        @Override
        public void run() {
            Bitmap detect = BitmapFactory.decodeFile(mPath);
            new File(mPath).delete();
            for (Bitmap sample : mSampleList) {
                compareFace(sample, detect); // 比较两张人脸的相似度
            }
            mPath = String.format("%s/%s.jpg",
                    getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), DateUtil.getNowDateTime());
            Log.d(TAG, "path="+mPath);
            mTransactor.saveFace(mPath); // 人脸办理器对象保存人脸
        }
    }

    // 将待检测人脸添加到已识别人脸
    private void addDetectFace(Bitmap detect, float similarity) {
        if (mDetectList.size() < 3) {
            mDetectList.add(new DetectedFace(detect, similarity));
        } else {
            DetectedFace lastDetect = mDetectList.get(mDetectList.size()-1);
            if (similarity > lastDetect.getSimilarity()) {
                mDetectList.remove(mDetectList.size()-1);
                mDetectList.add(new DetectedFace(detect, similarity));
            }
        }
        // 将已识别人脸列表按照相似度降序排列
        Collections.sort(mDetectList, (o1, o2) ->
                o2.getSimilarity().compareTo(o1.getSimilarity()));
        for (DetectedFace fate : mDetectList) {
            Log.d(TAG, "sort similarity="+fate.getSimilarity());
        }
    }

    // 初始化人脸分析器
    private void initAnalyzer() {
        MLFaceVerificationAnalyzerSetting setting = new MLFaceVerificationAnalyzerSetting.Factory()
                .setMaxFaceDetected(FACEMAX).create();
        mAnalyzer = MLFaceVerificationAnalyzerFactory.getInstance().getFaceVerificationAnalyzer(setting);
    }

    // 比较两张人脸的相似度
    private void compareFace(Bitmap firstBitmap, Bitmap secondBitmap) {
        if (firstBitmap==null || secondBitmap==null) {
            return;
        }
        mSecondBitmapCopy = secondBitmap.copy(Bitmap.Config.ARGB_8888, true);
        try {
            // 给人脸比对分析器设置待比较的人脸模板
            List<MLFaceTemplateResult> results = mAnalyzer
                    .setTemplateFace(MLFrame.fromBitmap(firstBitmap));
            // 给人脸比对分析器创建异步分析任务，准备分析比较指定人脸
            Task<List<MLFaceVerificationResult>> task = mAnalyzer
                    .asyncAnalyseFrame(MLFrame.fromBitmap(secondBitmap));
            // 给异步分析任务添加成功监听器和失败监听器
            task.addOnSuccessListener(compareList -> {
                for (MLFaceVerificationResult template : compareList) {
                    // 在位图上面标出人脸相框
                    BitmapUtil.drawFaceFrame(template.getFaceInfo().getFaceRect(), mSecondBitmapCopy);
                    runOnUiThread(() -> matchFace(template.getSimilarity())); // 匹配到人脸
                }
            }).addOnFailureListener(e -> Log.d(TAG, "人脸比对失败："+e.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    // 匹配到人脸
    private void matchFace(float similarity) {
        Log.d(TAG, "similarity="+similarity);
        if (similarity >= 0.9) {
            tv_option.setVisibility(View.VISIBLE);
            addDetectFace(mSecondBitmapCopy, similarity); // 添加到已识别人脸
        }
    }

}