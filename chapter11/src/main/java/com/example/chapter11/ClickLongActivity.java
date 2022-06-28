package com.example.chapter11;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.chapter11.widget.ClickView;

public class ClickLongActivity extends AppCompatActivity {
    private TextView tv_desc; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_long);
        tv_desc = findViewById(R.id.tv_desc);
        ClickView cv_gesture = findViewById(R.id.cv_gesture);
        // 设置点击视图的手势抬起监听器
        cv_gesture.setLiftListener((time_interval, pressure) -> {
            String gesture = time_interval>500 ? "长按" : "点击";
            String desc = String.format("本次按压时长为%d毫秒，属于%s动作。\n按压的压力峰值为%f",
                    time_interval, gesture, pressure);
            tv_desc.setText(desc);
        });
    }
}