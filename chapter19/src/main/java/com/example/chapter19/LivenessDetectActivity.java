package com.example.chapter19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCapture;
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCapture.Callback;
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCaptureResult;

public class LivenessDetectActivity extends AppCompatActivity {
    private final static String TAG = "LivenessDetectActivity";
    private ImageView iv_result;
    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveness_detect);
        tv_result = findViewById(R.id.tv_result);
        iv_result = findViewById(R.id.iv_result);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_capture).setOnClickListener(v -> startCaptureActivity());
    }

    // 启动HMS自带的活体检测页面
    private void startCaptureActivity() {
        MLLivenessCapture capture = MLLivenessCapture.getInstance();
        capture.startDetect(this, mCallback);
    }

    // 定义一个活体捕捉的回调对象
    public Callback mCallback = new Callback() {
        @Override
        public void onSuccess(MLLivenessCaptureResult result) {
            tv_result.setBackgroundColor(result.isLive()?Color.CYAN:Color.RED);
            String desc = String.format("活体检测结果为：%s。\n" +
                            "评分为%.2f，侧滑角为%.2f，倾斜角%.2f，卷曲度为%.2f",
                    result.isLive()?"真人":"假人", result.getScore(),
                    result.getYaw(), result.getPitch(), result.getRoll());
            tv_result.setText(desc);
            iv_result.setImageBitmap(result.getBitmap());
        }

        @Override
        public void onFailure(int errorCode) {
            tv_result.setText("识别出错，错误码为：" + errorCode);
        }
    };

}