package com.example.chapter03;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TextColorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_color);
        // 从布局文件中获取名叫tv_code_system的文本视图
        TextView tv_code_system = findViewById(R.id.tv_code_system);
        // 将tv_code_system的文字颜色设置系统自带的绿色
        tv_code_system.setTextColor(Color.GREEN);
        // 从布局文件中获取名叫tv_code_six的文本视图
        TextView tv_code_six = findViewById(R.id.tv_code_six);
        // 将tv_code_six的文字颜色设置为透明的绿色，透明就是看不到
        tv_code_six.setTextColor(0x00ff00);
        // 从布局文件中获取名叫tv_code_eight的文本视图
        TextView tv_code_eight = findViewById(R.id.tv_code_eight);
        // 将tv_code_eight的文字颜色设置为不透明的绿色，即正常的绿色
        tv_code_eight.setTextColor(0xff00ff00);
        // 从布局文件中获取名叫tv_code_background的文本视图
        TextView tv_code_background = findViewById(R.id.tv_code_background);
        // 将tv_code_background的背景颜色设置为绿色
        tv_code_background.setBackgroundColor(Color.GREEN); // 在代码中定义的色值
        tv_code_background.setBackgroundResource(R.color.green); // 颜色来自资源文件
    }
}
