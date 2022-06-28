package com.example.chapter11;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.example.chapter11.util.PointUtil;
import com.example.chapter11.widget.MultiTouchView;

@SuppressLint("DefaultLocale")
public class ScaleRotateActivity extends AppCompatActivity {
    private TextView tv_desc; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale_rotate);
        tv_desc = findViewById(R.id.tv_desc);
        MultiTouchView mtv_gesture = findViewById(R.id.mtv_gesture);
        // 设置多点触摸视图的手势滑动监听器
        mtv_gesture.setSlideListener((firstBeginP, firstEndP, secondBeginP, secondEndP) -> {
            // 上次两个触摸点之间的距离
            float preWholeDistance = PointUtil.distance(firstBeginP, secondBeginP);
            // 当前两个触摸点之间的距离
            float nowWholeDistance = PointUtil.distance(firstEndP, secondEndP);
            // 主要点在前后两次落点之间的距离
            float primaryDistance = PointUtil.distance(firstBeginP, firstEndP);
            // 次要点在前后两次落点之间的距离
            float secondaryDistance = PointUtil.distance(secondBeginP, secondEndP);
            if (Math.abs(nowWholeDistance - preWholeDistance) >
                    (float) Math.sqrt(2) / 2.0f * (primaryDistance + secondaryDistance)) {
                // 倾向于在原始线段的相同方向上移动，则判作缩放动作
                float scaleRatio = nowWholeDistance / preWholeDistance;
                String desc = String.format("本次手势为缩放动作，%s为%f",
                        scaleRatio>=1?"放大倍数":"缩小比例", scaleRatio);
                tv_desc.setText(desc);
            } else { // 倾向于在原始线段的垂直方向上移动，则判作旋转动作
                // 计算上次触摸事件的旋转角度
                int preDegree = PointUtil.degree(firstBeginP, secondBeginP);
                // 计算本次触摸事件的旋转角度
                int nowDegree = PointUtil.degree(firstEndP, secondEndP);
                String desc = String.format("本次手势为旋转动作，%s方向旋转了%d度",
                        nowDegree>preDegree?"顺时针":"逆时针", Math.abs(nowDegree-preDegree));
                tv_desc.setText(desc);
            }
        });
    }

}