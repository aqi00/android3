package com.example.chapter13;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chapter13.util.PermissionUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_handler_message).setOnClickListener(this);
        findViewById(R.id.btn_thread_ui).setOnClickListener(this);
        findViewById(R.id.btn_work_manager).setOnClickListener(this);
        findViewById(R.id.btn_json_convert).setOnClickListener(this);
        findViewById(R.id.btn_okhttp_call).setOnClickListener(this);
        findViewById(R.id.btn_okhttp_download).setOnClickListener(this);
        findViewById(R.id.btn_okhttp_upload).setOnClickListener(this);
        findViewById(R.id.btn_glide_simple).setOnClickListener(this);
        findViewById(R.id.btn_glide_cache).setOnClickListener(this);
        findViewById(R.id.btn_glide_special).setOnClickListener(this);
        findViewById(R.id.btn_socketio_text).setOnClickListener(this);
        findViewById(R.id.btn_socketio_image).setOnClickListener(this);
        findViewById(R.id.btn_web_socket).setOnClickListener(this);
        findViewById(R.id.btn_we_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_handler_message) {
            startActivity(new Intent(this, HandlerMessageActivity.class));
        } else if (v.getId() == R.id.btn_thread_ui) {
            startActivity(new Intent(this, ThreadUiActivity.class));
        } else if (v.getId() == R.id.btn_work_manager) {
            startActivity(new Intent(this, WorkManagerActivity.class));
        } else if (v.getId() == R.id.btn_json_convert) {
            startActivity(new Intent(this, JsonConvertActivity.class));
        } else if (v.getId() == R.id.btn_okhttp_call) {
            startActivity(new Intent(this, OkhttpCallActivity.class));
        } else if (v.getId() == R.id.btn_okhttp_download) {
            startActivity(new Intent(this, OkhttpDownloadActivity.class));
        } else if (v.getId() == R.id.btn_okhttp_upload) {
            startActivity(new Intent(this, OkhttpUploadActivity.class));
        } else if (v.getId() == R.id.btn_glide_simple) {
            startActivity(new Intent(this, GlideSimpleActivity.class));
        } else if (v.getId() == R.id.btn_glide_cache) {
            startActivity(new Intent(this, GlideCacheActivity.class));
        } else if (v.getId() == R.id.btn_glide_special) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, GlideSpecialActivity.class));
            }
        } else if (v.getId() == R.id.btn_socketio_text) {
            startActivity(new Intent(this, SocketioTextActivity.class));
        } else if (v.getId() == R.id.btn_socketio_image) {
            startActivity(new Intent(this, SocketioImageActivity.class));
        } else if (v.getId() == R.id.btn_web_socket) {
            startActivity(new Intent(this, WebSocketActivity.class));
        } else if (v.getId() == R.id.btn_we_login) {
            startActivity(new Intent(this, WeLoginActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // requestCode不能为负数，也不能大于2的16次方即65536
        if (requestCode == R.id.btn_glide_special % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, GlideSpecialActivity.class));
            } else {
                Toast.makeText(this, "需要允许存储卡权限才能浏览相册图片噢", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
