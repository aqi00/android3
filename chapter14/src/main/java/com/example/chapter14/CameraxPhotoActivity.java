package com.example.chapter14;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter14.util.Utils;
import com.example.chapter14.widget.CameraXView;

public class CameraxPhotoActivity extends AppCompatActivity {
    private final static String TAG = "CameraxPhotoActivity";
    private CameraXView cxv_preview; // 声明一个增强相机视图对象
    private View v_black; // 声明一个视图对象
    private ImageView iv_photo; // 声明一个图像视图对象
    private final Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax_photo);
        initView(); // 初始化视图
        initCamera(); // 初始化相机
    }

    // 初始化视图
    private void initView() {
        cxv_preview = findViewById(R.id.cxv_preview);
        v_black = findViewById(R.id.v_black);
        iv_photo = findViewById(R.id.iv_photo);
        iv_photo.setOnClickListener((v) -> dealPhoto()); // 处理拍照动作
        findViewById(R.id.iv_switch).setOnClickListener((v) -> cxv_preview.switchCamera());
        findViewById(R.id.btn_album).setOnClickListener((v) -> {
            // 下面跳到系统相册界面
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            startActivity(intent);
        });
        findViewById(R.id.btn_view).setOnClickListener((v) -> {
            if (TextUtils.isEmpty(cxv_preview.getPhotoPath())) {
                Toast.makeText(this, "请先拍照再浏览照片", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, PhotoDetailActivity.class);
            intent.putExtra("photo_path", cxv_preview.getPhotoPath());
            startActivity(intent);
        });
        int adjustHeight = Utils.getScreenWidth(this) * 16 / 9;
        Log.d(TAG, "onResume getScreenWidth="+Utils.getScreenWidth(this)+", adjustHeight="+adjustHeight);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cxv_preview.getLayoutParams();
        params.height = adjustHeight; // 根据预览尺寸调整预览窗口的高度
        cxv_preview.setLayoutParams(params); // 设置预览视图的布局参数
    }

    // 初始化相机
    private void initCamera() {
        // 打开增强相机，并指定停止拍照监听器
        cxv_preview.openCamera(this, CameraXView.MODE_PHOTO, (result) -> {
            runOnUiThread(() -> {
                iv_photo.setEnabled(true);
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            });
        });
    }

    // 处理拍照动作
    private void dealPhoto() {
        iv_photo.setEnabled(false);
        v_black.setVisibility(View.VISIBLE);
        cxv_preview.takePicture(); // 拍摄照片
        mHandler.postDelayed(() -> v_black.setVisibility(View.GONE), 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cxv_preview.closeCamera(); // 关闭相机
    }

}