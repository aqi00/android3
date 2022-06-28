package com.example.chapter19;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter19.util.Utils;
import com.huawei.hms.hmsscankit.RemoteView;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

public class HmsScanActivity extends AppCompatActivity {
    private static final String TAG = "HmsScanActivity";
    private RelativeLayout rl_scan;
    private ImageView iv_back;
    private ImageView iv_album;
    private ImageView iv_flash;
    private RemoteView remoteView; // 声明一个HMS的远程视图对象
    private int SCAN_FRAME_SIZE = 240; // 扫码框的默认尺寸
    private static final int REQUEST_CODE_PHOTO = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hms_scan);
        rl_scan = findViewById(R.id.rl_scan);
        iv_back = findViewById(R.id.iv_back);
        iv_flash = findViewById(R.id.iv_flash);
        iv_album = findViewById(R.id.iv_album);
        iv_back.setOnClickListener(v -> finish());
        iv_album.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(pickIntent, REQUEST_CODE_PHOTO);
        });
        iv_flash.setOnClickListener(v -> {
            if (remoteView.getLightStatus()) {
                remoteView.switchLight();
                iv_flash.setImageResource(R.drawable.flashlight_off);
            } else {
                remoteView.switchLight();
                iv_flash.setImageResource(R.drawable.flashlight_on);
            }
        });
        addRemoteView(savedInstanceState); // 添加扫码的远程视图
    }

    // 添加扫码的远程视图
    private void addRemoteView(Bundle savedInstanceState) {
        int screenWidth = Utils.getScreenWidth(this); // 获取屏幕宽度
        int screenHeight = Utils.getScreenHeight(this); // 获取屏幕高度
        int scanFrameSize = (int) (SCAN_FRAME_SIZE * Utils.getScreenDensity(this));

        // 计算取景器的四周边缘。如果没有指定设置，它将位于布局的中间位置。
        Rect rect = new Rect();
        rect.left = screenWidth / 2 - scanFrameSize / 2;
        rect.right = screenWidth / 2 + scanFrameSize / 2;
        rect.top = screenHeight / 2 - scanFrameSize / 2;
        rect.bottom = screenHeight / 2 + scanFrameSize / 2;

        // 初始化远程视图实例
        remoteView = new RemoteView.Builder().setContext(this)
                .setBoundingBox(rect).setFormat(HmsScan.ALL_SCAN_TYPE).build();
        // 当光线昏暗时，展示闪光灯开关按钮，以便用户决定是否开灯
        remoteView.setOnLightVisibleCallback(visible -> {
            if (visible) {
                iv_flash.setVisibility(View.VISIBLE);
            }
        });
        // 设置扫描结果的回调事件
        remoteView.setOnResultCallback(result -> showResult(result));
        // 将自定义视图加载到活动中.
        remoteView.onCreate(savedInstanceState);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rl_scan.addView(remoteView, params); // 往相对布局添加远程视图
    }

    @Override
    protected void onStart() {
        super.onStart();
        remoteView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        remoteView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        remoteView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        remoteView.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        remoteView.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                HmsScan[] hmsScans = ScanUtil.decodeWithBitmap(this, bitmap,
                        new HmsScanAnalyzerOptions.Creator().setPhotoMode(true).create());
                showResult(hmsScans); // 显示扫码识别结果
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 显示扫码识别结果
    private void showResult(HmsScan[] result) {
        if (result != null && result.length > 0 && result[0] != null &&
                !TextUtils.isEmpty(result[0].getOriginalValue())) {
            Intent intent = new Intent(this, ScanResultActivity.class);
            intent.putExtra(ScanUtil.RESULT, result[0]);
            startActivity(intent); // 跳转到扫码结果页
        }
    }
}