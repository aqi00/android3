package com.example.chapter15;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chapter15.util.PermissionUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_web_local).setOnClickListener(this);
        findViewById(R.id.btn_web_panorama).setOnClickListener(this);
        findViewById(R.id.btn_web_browser).setOnClickListener(this);
        findViewById(R.id.btn_web_video).setOnClickListener(this);
        findViewById(R.id.btn_epub_reader).setOnClickListener(this);
        findViewById(R.id.btn_pdf_render).setOnClickListener(this);
        findViewById(R.id.btn_pdf_slide).setOnClickListener(this);
        findViewById(R.id.btn_pdf_curve).setOnClickListener(this);
        findViewById(R.id.btn_read_word).setOnClickListener(this);
        findViewById(R.id.btn_read_ppt).setOnClickListener(this);
        findViewById(R.id.btn_tbs_document).setOnClickListener(this);
        findViewById(R.id.btn_tbs_webpage).setOnClickListener(this);
        findViewById(R.id.btn_jni_cpu).setOnClickListener(this);
        findViewById(R.id.btn_jni_secret).setOnClickListener(this);
        findViewById(R.id.btn_ebook_reader).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_web_local) {
            startActivity(new Intent(this, WebLocalActivity.class));
        } else if (v.getId() == R.id.btn_web_panorama) {
            startActivity(new Intent(this, WebPanoramaActivity.class));
        } else if (v.getId() == R.id.btn_web_browser) {
            startActivity(new Intent(this, WebBrowserActivity.class));
        } else if (v.getId() == R.id.btn_web_video) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, WebVideoActivity.class));
            }
        } else if (v.getId() == R.id.btn_epub_reader) {
            startActivity(new Intent(this, EpubReaderActivity.class));
        } else if (v.getId() == R.id.btn_web_video) {
            startActivity(new Intent(this, WebVideoActivity.class));
        } else if (v.getId() == R.id.btn_pdf_render) {
            startActivity(new Intent(this, PdfRenderActivity.class));
        } else if (v.getId() == R.id.btn_pdf_slide) {
            startActivity(new Intent(this, PdfSlideActivity.class));
        } else if (v.getId() == R.id.btn_pdf_curve) {
            startActivity(new Intent(this, PdfCurveActivity.class));
        } else if (v.getId() == R.id.btn_read_word) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, ReadWordActivity.class));
            }
        } else if (v.getId() == R.id.btn_read_ppt) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, ReadPptActivity.class));
            }
        } else if (v.getId() == R.id.btn_tbs_document) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, TbsDocumentActivity.class));
            }
        } else if (v.getId() == R.id.btn_tbs_webpage) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, TbsWebpageActivity.class));
            }
        } else if (v.getId() == R.id.btn_jni_cpu) {
            startActivity(new Intent(this, JniCpuActivity.class));
        } else if (v.getId() == R.id.btn_jni_secret) {
            startActivity(new Intent(this, JniSecretActivity.class));
        } else if (v.getId() == R.id.btn_ebook_reader) {
            startActivity(new Intent(this, EbookReaderActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // requestCode不能为负数，也不能大于2的16次方即65536
        if (requestCode == R.id.btn_web_video % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, WebVideoActivity.class));
            } else {
                Toast.makeText(this, "需要允许存储卡权限才能浏览网页视频噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_read_word % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, ReadWordActivity.class));
            } else {
                Toast.makeText(this, "需要允许存储卡权限才能浏览Word噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_read_ppt % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, ReadPptActivity.class));
            } else {
                Toast.makeText(this, "需要允许存储卡权限才能浏览PPT噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_tbs_document % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, TbsDocumentActivity.class));
            } else {
                Toast.makeText(this, "需要允许存储卡权限才能浏览文档噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_tbs_webpage % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, TbsWebpageActivity.class));
            } else {
                Toast.makeText(this, "需要允许存储卡权限才能浏览网页噢", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
