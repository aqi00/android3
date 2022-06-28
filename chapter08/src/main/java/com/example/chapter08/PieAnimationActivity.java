package com.example.chapter08;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter08.widget.PieAnimation;

public class PieAnimationActivity extends AppCompatActivity implements View.OnClickListener {
    private PieAnimation pa_circle; // 声明一个饼图动画对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_animation);
        // 从布局文件中获取名叫pa_circle的饼图动画
        pa_circle = findViewById(R.id.pa_circle);
        pa_circle.setOnClickListener(this); // 设置饼图动画的点击监听器
        pa_circle.start(); // 开始播放饼图动画
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pa_circle) {
            if (!pa_circle.isRunning()) { // 判断饼图动画是否正在播放
                pa_circle.start(); // 不在播放，则开始播放饼图动画
            }
        }
    }

}
