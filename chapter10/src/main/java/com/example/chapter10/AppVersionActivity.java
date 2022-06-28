package com.example.chapter10;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("DefaultLocale")
public class AppVersionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_version);
        ImageView iv_icon = findViewById(R.id.iv_icon);
        iv_icon.setImageResource(R.mipmap.ic_launcher); // 应用图标取自ic_launcher
        TextView tv_desc = findViewById(R.id.tv_desc);
        // 应用名称取自app_name，应用包名、版本号、版本名称均来自BuildConfig
        String desc = String.format("App名称为：%s\nApp包名为：%s\n" +
                        "App版本号为：%d\nApp版本名称为：%s",
                getString(R.string.app_name), BuildConfig.APPLICATION_ID,
                BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME);
        tv_desc.setText(desc);
    }

}
