package com.example.chapter11;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class TouchSingleActivity extends AppCompatActivity {
    private TextView tv_touch; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_single);
        tv_touch = findViewById(R.id.tv_touch);
    }

    // 在发生触摸事件时触发
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 从开机到现在的毫秒数
        int seconds = (int) (event.getEventTime() / 1000);
        String desc = String.format("动作发生时间：开机距离现在%02d:%02d:%02d",
                seconds / 3600, seconds % 3600 / 60, seconds % 60);
        desc = String.format("%s\n动作名称是：", desc);
        int action = event.getAction(); // 获得触摸事件的动作类型
        if (action == MotionEvent.ACTION_DOWN) { // 按下手指
            desc = String.format("%s按下", desc);
        } else if (action == MotionEvent.ACTION_MOVE) { // 移动手指
            desc = String.format("%s移动", desc);
        } else if (action == MotionEvent.ACTION_UP) { // 松开手指
            desc = String.format("%s提起", desc);
        } else if (action == MotionEvent.ACTION_CANCEL) { // 取消手势
            desc = String.format("%s取消", desc);
        }
        desc = String.format("%s\n动作发生位置是：横坐标%f，纵坐标%f，压力为%f",
                desc, event.getX(), event.getY(), event.getPressure());
        tv_touch.setText(desc);
        return super.onTouchEvent(event);
    }

}
