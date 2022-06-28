package com.example.chapter19;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chapter19.util.Utils;
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessCaptureResult;
import com.huawei.hms.mlsdk.livenessdetection.MLLivenessDetectView;
import com.huawei.hms.mlsdk.livenessdetection.OnMLLivenessDetectCallback;

public class LivenessCustomActivity extends AppCompatActivity {
    private TextView tv_result;
    private MLLivenessDetectView mLivenessView; // 声明一个活体检测视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liveness_custom);
        tv_result = findViewById(R.id.tv_result);
        RelativeLayout rl_surface = findViewById(R.id.rl_surface);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        Rect rect = new Rect(0, 0, Utils.getScreenWidth(this), Utils.dip2px(this, 480));
        // 根据指定参数建造一个活体检测视图
        mLivenessView = new MLLivenessDetectView.Builder().setContext(this)
                .setOptions(MLLivenessDetectView.DETECT_MASK)
                .setFaceFrameRect(rect) // 设置人脸相框的矩形边界
                // 设置活体检测的回调监听器
                .setDetectCallback(new MyLivenessCallback()).build();
        rl_surface.addView(mLivenessView); // 把活体检测视图添加至相对布局
        mLivenessView.onCreate(savedInstanceState); // 创建活体检测视图对象
    }

    // 定义一个活体检测的回调类
    private class MyLivenessCallback implements OnMLLivenessDetectCallback {
        @Override
        public void onCompleted(MLLivenessCaptureResult result) {
            tv_result.setBackgroundColor(result.isLive()? Color.rgb(100,255,255):Color.rgb(255,255,0));
            String desc = String.format("活体检测结果为：%s。\n" +
                            "评分为%.2f，侧滑角为%.2f，倾斜角%.2f，卷曲度为%.2f",
                    result.isLive()?"真人":"假人", result.getScore(),
                    result.getYaw(), result.getPitch(), result.getRoll());
            tv_result.setText(desc);
        }

        @Override
        public void onError(int error) {
            tv_result.setText("识别出错，错误码为：" + error);
        }

        public void onInfo(int infoCode, Bundle bundle) {}

        @Override
        public void onStateChange(int state, Bundle bundle) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLivenessView.onDestroy(); // 销毁活体检测视图对象
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLivenessView.onPause(); // 暂停活体检测视图对象
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLivenessView.onResume(); // 恢复活体检测视图对象
    }

}