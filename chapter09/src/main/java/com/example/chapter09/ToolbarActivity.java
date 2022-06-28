package com.example.chapter09;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ToolbarActivity extends AppCompatActivity {
    private final static String TAG = "ToolbarActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);
        Toolbar tl_head = findViewById(R.id.tl_head); // 从布局文件中获取名叫tl_head的工具栏
        tl_head.setTitle("工具栏页面"); // 设置工具栏的标题文本
        setSupportActionBar(tl_head); // 使用tl_head替换系统自带的ActionBar
        tl_head.setTitleTextColor(Color.RED); // 设置工具栏的标题文字颜色
        tl_head.setLogo(R.drawable.ic_app); // 设置工具栏的标志图片
        tl_head.setSubtitle("Toolbar"); // 设置工具栏的副标题文本
        tl_head.setSubtitleTextColor(Color.YELLOW); // 设置工具栏的副标题文字颜色
        tl_head.setBackgroundResource(R.color.blue_light); // 设置工具栏的背景
        tl_head.setNavigationIcon(R.drawable.ic_back); // 设置工具栏左边的导航图标
        // 给tl_head设置导航图标的点击监听器
        // setNavigationOnClickListener必须放到setSupportActionBar之后，不然不起作用
        tl_head.setNavigationOnClickListener(view -> {
            finish(); // 结束当前页面
        });
    }

}
