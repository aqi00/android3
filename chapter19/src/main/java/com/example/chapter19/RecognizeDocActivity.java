package com.example.chapter19;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chapter19.util.BitmapUtil;
import com.example.hmsml.text.callback.CommonResultCallBack;
import com.example.hmsml.text.callback.CouldInfoResultCallBack;
import com.example.hmsml.text.transactor.ImageTransactor;
import com.example.hmsml.text.transactor.RemoteTextTransactor;
import com.example.hmsml.text.views.overlay.GraphicOverlay;

public class RecognizeDocActivity extends AppCompatActivity {
    private final static String TAG = "RecognizeDocActivity";
    private ImageView iv_origin;
    private TextView tv_result;
    private ProgressDialog mDialog; // 声明一个进度对话框对象
    private final Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private GraphicOverlay go_overlay; // 声明一个图形覆盖板对象
    private RemoteTextTransactor mRemoteTransactor; // 声明一个远程文本办理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize_doc);
        iv_origin = findViewById(R.id.iv_origin);
        tv_result = findViewById(R.id.tv_result);
        go_overlay = findViewById(R.id.go_overlay);
        initImageTransactor(); // 初始化远程文本办理器
    }

    // 初始化远程文本办理器
    private void initImageTransactor() {
        // 创建一个识别结果的回调事件对象
        CouldInfoResultCallBack resultCallBack = new CommonResultCallBack(result -> {
            if (mDialog!=null && mDialog.isShowing()) {
                mDialog.dismiss(); // 识别结束，关闭进度对话框
            }
            tv_result.setText(result);
        });
        mRemoteTransactor = new RemoteTextTransactor(mHandler);
        // 给远程文本办理器添加识别结果的回调事件
        mRemoteTransactor.addCouldTextResultCallBack(resultCallBack);
//        mRemoteTransactor = new DocumentTextTransactor(mHandler);
//        ((DocumentTextTransactor) mRemoteTransactor).addCouldTextResultCallBack(resultCallBack);
        // 注册一个善后工作的活动结果启动器，获取指定类型的内容
        ActivityResultLauncher launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                // 根据指定图片的uri，获得自动缩小后的位图对象
                Bitmap bitmap = BitmapUtil.getAutoZoomImage(this, uri);
                // 远程文本办理器开始分析位图并从中识别文档
                mRemoteTransactor.process(bitmap, go_overlay);
                mDialog = ProgressDialog.show(this, "请稍等", "正在努力识别文档");
                iv_origin.setImageBitmap(bitmap);
            }
        });
        findViewById(R.id.btn_choose).setOnClickListener(v -> launcher.launch("image/*"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRemoteTransactor != null) {
            mRemoteTransactor.stop();
            mRemoteTransactor = null;
        }
    }

}