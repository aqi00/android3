package com.example.chapter15;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebPanoramaActivity extends AppCompatActivity {
    private WebView wv_panorama; // 声明一个网页视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_panorama);
        wv_panorama = findViewById(R.id.wv_panorama);
        findViewById(R.id.btn_load).setOnClickListener(v -> loadWeb());
    }

    // 加载本地的全景网页
    private void loadWeb() {
        // 命令网页视图加载指定路径的网页
        wv_panorama.loadUrl("file:///android_asset/panorama/index.html");
        // 获取网页视图的网页设置
        WebSettings settings = wv_panorama.getSettings();
        // 是否支持Javascript
        settings.setJavaScriptEnabled(true);
        // 给网页视图设置默认的网页浏览客户端
        wv_panorama.setWebViewClient(new WebViewClient());
    }

}