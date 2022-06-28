package com.example.chapter03;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TextSizeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_size);
        // 从布局文件中获取名叫tv_sp的文本视图
        TextView tv_sp = findViewById(R.id.tv_sp);
        tv_sp.setTextSize(30); // 设置tv_sp的文本大小
    }
}
