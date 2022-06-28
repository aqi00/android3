package com.example.chapter12;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

public class FrameAnimActivity extends AppCompatActivity {
    private ImageView iv_frame_anim; // 声明一个图像视图对象
    private AnimationDrawable ad_frame; // 声明一个帧动画对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_anim);
        iv_frame_anim = findViewById(R.id.iv_frame_anim);
        iv_frame_anim.setOnClickListener(v -> {
            if (ad_frame.isRunning()) {  // 判断帧动画是否正在播放
                ad_frame.stop(); // 停止播放帧动画
            } else {
                ad_frame.start(); // 开始播放帧动画
            }
        });
        showFrameAnimByCode(); // 在代码中生成并播放帧动画
        //showFrameAnimByXml(); // 从xml文件获取并播放帧动画
    }

    // 在代码中生成并播放帧动画
    private void showFrameAnimByCode() {
        ad_frame = new AnimationDrawable(); // 创建一个帧动画图形
        // 下面把每帧图片加入到帧动画的列表中
        ad_frame.addFrame(getDrawable(R.drawable.flow_p1), 50);
        ad_frame.addFrame(getDrawable(R.drawable.flow_p2), 50);
        ad_frame.addFrame(getDrawable(R.drawable.flow_p3), 50);
        ad_frame.addFrame(getDrawable(R.drawable.flow_p4), 50);
        ad_frame.addFrame(getDrawable(R.drawable.flow_p5), 50);
        ad_frame.addFrame(getDrawable(R.drawable.flow_p6), 50);
        ad_frame.addFrame(getDrawable(R.drawable.flow_p7), 50);
        ad_frame.addFrame(getDrawable(R.drawable.flow_p8), 50);
        // 设置帧动画是否只播放一次。为true表示只播放一次，为false表示循环播放
        ad_frame.setOneShot(false);
        // 设置图像视图的图形为帧动画
        iv_frame_anim.setImageDrawable(ad_frame);
        ad_frame.start(); // 开始播放帧动画
    }

    // 从xml文件获取并播放帧动画
    private void showFrameAnimByXml() {
        // 设置图像视图的图像来源为帧动画的XML定义文件
        iv_frame_anim.setImageResource(R.drawable.frame_anim);
        // 从图像视图对象中获取帧动画
        ad_frame = (AnimationDrawable) iv_frame_anim.getDrawable();
        ad_frame.start(); // 开始播放帧动画
    }

}
