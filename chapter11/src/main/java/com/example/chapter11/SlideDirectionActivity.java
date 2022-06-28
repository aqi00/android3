package com.example.chapter11;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.chapter11.util.DateUtil;
import com.example.chapter11.widget.SingleTouchView;

public class SlideDirectionActivity extends AppCompatActivity {
    private TextView tv_desc; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_direction);
        tv_desc = findViewById(R.id.tv_desc);
        SingleTouchView stv_gesture = findViewById(R.id.stv_gesture);
        // 设置单点触摸视图的手势飞掠监听器
        stv_gesture.setFlipListener((beginPos, endPos) -> {
            float offsetX = Math.abs(endPos.x - beginPos.x);
            float offsetY = Math.abs(endPos.y - beginPos.y);
            String gesture = "";
            if (offsetX > offsetY) { // 水平方向滑动
                gesture = (endPos.x - beginPos.x > 0) ? "向右" : "向左";
            } else if (offsetX < offsetY) { // 垂直方向滑动
                gesture = (endPos.y - beginPos.y > 0) ? "向下" : "向上";
            } else { // 对角线滑动
                gesture = "对角线";
            }
            String desc = String.format("%s 本次手势为%s滑动", DateUtil.getNowTime(), gesture);
            tv_desc.setText(desc);
        });
    }
}