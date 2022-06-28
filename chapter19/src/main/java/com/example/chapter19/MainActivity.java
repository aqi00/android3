package com.example.chapter19;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.chapter19.util.PermissionUtil;
import com.huawei.agconnect.AGConnectOptions;
import com.huawei.agconnect.AGConnectOptionsBuilder;
import com.huawei.hms.mlsdk.common.MLApplication;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_hms_scan).setOnClickListener(this);
        findViewById(R.id.btn_recognize_text).setOnClickListener(this);
        findViewById(R.id.btn_recognize_doc).setOnClickListener(this);
        findViewById(R.id.btn_face_detect).setOnClickListener(this);
        findViewById(R.id.btn_face_verify).setOnClickListener(this);
        findViewById(R.id.btn_livness_detect).setOnClickListener(this);
        findViewById(R.id.btn_livness_custom).setOnClickListener(this);
        findViewById(R.id.btn_face_pick).setOnClickListener(this);
        findViewById(R.id.btn_bg_replace).setOnClickListener(this);
        findViewById(R.id.btn_face_smile).setOnClickListener(this);
        findViewById(R.id.btn_wisdom_eye).setOnClickListener(this);
        setApiKey(); // 设置HMS的API_KEY
    }

    public static final String API_KEY = "client/api_key";
    private void setApiKey(){ // 设置HMS的API_KEY
        AGConnectOptions options = new AGConnectOptionsBuilder().build(this);
        MLApplication.getInstance().setApiKey(options.getString(API_KEY));
        Log.d(TAG, "API_KEY="+options.getString(API_KEY));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_hms_scan) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.CAMERA}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, HmsScanActivity.class));
            }
        } else if (v.getId() == R.id.btn_recognize_text) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.CAMERA}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, RecognizeTextActivity.class));
            }
        } else if (v.getId() == R.id.btn_recognize_doc) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, RecognizeDocActivity.class));
            }
        } else if (v.getId() == R.id.btn_face_detect) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.CAMERA}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, FaceDetectActivity.class));
            }
        } else if (v.getId() == R.id.btn_face_verify) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, FaceVerifyActivity.class));
            }
        } else if (v.getId() == R.id.btn_livness_detect) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.CAMERA}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, LivenessDetectActivity.class));
            }
        } else if (v.getId() == R.id.btn_livness_custom) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.CAMERA}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, LivenessCustomActivity.class));
            }
        } else if (v.getId() == R.id.btn_face_pick) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, FacePickActivity.class));
            }
        } else if (v.getId() == R.id.btn_bg_replace) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, BgReplaceActivity.class));
            }
        } else if (v.getId() == R.id.btn_face_smile) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, FaceSmileActivity.class));
            }
        } else if (v.getId() == R.id.btn_wisdom_eye) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, WisdomEyeActivity.class));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // requestCode不能为负数，也不能大于2的16次方即65536
        if (requestCode == R.id.btn_hms_scan % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, HmsScanActivity.class));
            } else {
                Toast.makeText(this, "需要允许摄像头权限才能扫描二维码噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_recognize_text % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, RecognizeTextActivity.class));
            } else {
                Toast.makeText(this, "需要允许摄像头权限才能识别文本噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_recognize_doc % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, RecognizeDocActivity.class));
            } else {
                Toast.makeText(this, "需要允许存储卡权限才能识别文档噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_face_detect % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, FaceDetectActivity.class));
            } else {
                Toast.makeText(this, "需要允许摄像头权限才能检测人脸噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_face_verify % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, FaceVerifyActivity.class));
            } else {
                Toast.makeText(this, "需要允许存储卡权限才能比对人脸噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_livness_detect % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, LivenessDetectActivity.class));
            } else {
                Toast.makeText(this, "需要允许摄像头权限才能检测活体噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_livness_custom % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, LivenessCustomActivity.class));
            } else {
                Toast.makeText(this, "需要允许摄像头权限才能检测活体噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_face_pick % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, FacePickActivity.class));
            } else {
                Toast.makeText(this, "需要允许存储卡权限才能抠取人像噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_bg_replace % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, BgReplaceActivity.class));
            } else {
                Toast.makeText(this, "需要允许摄像头和存储卡权限才能替换背景噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_face_smile % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, FaceSmileActivity.class));
            } else {
                Toast.makeText(this, "需要允许摄像头和存储卡权限才能捕捉笑脸噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_wisdom_eye % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, WisdomEyeActivity.class));
            } else {
                Toast.makeText(this, "需要允许存储卡、摄像头和定位权限才能追踪人脸噢", Toast.LENGTH_SHORT).show();
            }
        }
    }
}