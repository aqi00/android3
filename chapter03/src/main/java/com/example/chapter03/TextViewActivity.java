package com.example.chapter03;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

public class TextViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 当前的页面布局采用的是res/layout/activity_text_view.xml
        setContentView(R.layout.activity_text_view);
        // 获取名叫tv_hello的文本视图
        TextView tv_hello = findViewById(R.id.tv_hello);
        tv_hello.setText("你好，世界"); // 设置tv_hello的文字内容
        //tv_hello.setText(R.string.hello); // 设置tv_hello的文字资源
    }
}
