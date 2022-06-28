package com.example.chapter05;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class DrawableShapeActivity extends AppCompatActivity implements View.OnClickListener {
    private View v_content; // 声明一个视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawable_shape);
        // 从布局文件中获取名叫v_content的视图
        v_content = findViewById(R.id.v_content);
        // v_content的背景设置为圆角矩形
        v_content.setBackgroundResource(R.drawable.shape_rect_gold);
        // 给btn_rect设置点击监听器
        findViewById(R.id.btn_rect).setOnClickListener(this);
        // 给btn_oval设置点击监听器
        findViewById(R.id.btn_oval).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_rect) { // 点击了“圆角矩形背景”按钮
            // v_content的背景设置为圆角矩形
            v_content.setBackgroundResource(R.drawable.shape_rect_gold);
        } else if (v.getId() == R.id.btn_oval) { // 点击了“椭圆背景”按钮
            // v_content的背景设置为椭圆形状
            v_content.setBackgroundResource(R.drawable.shape_oval_rose);
        }
    }

}
