package com.example.chapter12;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class AnimSetActivity extends AppCompatActivity implements AnimationListener {
    private ImageView iv_anim_set; // 声明一个图像视图对象
    private AnimationSet setAnim; // 声明一个集合动画对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim_set);
        iv_anim_set = findViewById(R.id.iv_anim_set);
        iv_anim_set.setOnClickListener(v -> startAnim());
        initAnimation(); // 初始化集合动画
    }

    // 初始化集合动画
    private void initAnimation() {
        // 创建一个灰度动画
        Animation alphaAnim = new AlphaAnimation(1.0f, 0.1f);
        alphaAnim.setDuration(3000); // 设置动画的播放时长
        alphaAnim.setFillAfter(true); // 设置维持结束画面
        // 创建一个平移动画
        Animation translateAnim = new TranslateAnimation(1.0f, -200f, 1.0f, 1.0f);
        translateAnim.setDuration(3000); // 设置动画的播放时长
        translateAnim.setFillAfter(true); // 设置维持结束画面
        // 创建一个缩放动画
        Animation scaleAnim = new ScaleAnimation(1.0f, 1.0f, 1.0f, 0.5f);
        scaleAnim.setDuration(3000); // 设置动画的播放时长
        scaleAnim.setFillAfter(true); // 设置维持结束画面
        // 创建一个旋转动画
        Animation rotateAnim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(3000); // 设置动画的播放时长
        rotateAnim.setFillAfter(true); // 设置维持结束画面
        // 创建一个集合动画
        setAnim = new AnimationSet(true);
        // 下面在代码中添加集合动画
        setAnim.addAnimation(alphaAnim); // 给集合动画添加灰度动画
        setAnim.addAnimation(translateAnim); // 给集合动画添加平移动画
        setAnim.addAnimation(scaleAnim); // 给集合动画添加缩放动画
        setAnim.addAnimation(rotateAnim); // 给集合动画添加旋转动画
        setAnim.setFillAfter(true); // 设置维持结束画面
        startAnim(); // 开始播放集合动画
    }

    // 开始播放集合动画
    private void startAnim() {
        iv_anim_set.startAnimation(setAnim); // 开始播放动画
        setAnim.setAnimationListener(this); // 设置动画事件监听器
    }

    // 在补间动画开始播放时触发
    @Override
    public void onAnimationStart(Animation animation) {}

    // 在补间动画结束播放时触发
    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation.equals(setAnim)) { // 原集合动画播放完毕，接着播放倒过来的集合动画
            // 创建一个灰度动画
            Animation alphaAnim2 = new AlphaAnimation(0.1f, 1.0f);
            alphaAnim2.setDuration(3000); // 设置动画的播放时长
            alphaAnim2.setFillAfter(true); // 设置维持结束画面
            // 创建一个平移动画
            Animation translateAnim2 = new TranslateAnimation(-200f, 1.0f, 1.0f, 1.0f);
            translateAnim2.setDuration(3000); // 设置动画的播放时长
            translateAnim2.setFillAfter(true); // 设置维持结束画面
            // 创建一个缩放动画
            Animation scaleAnim2 = new ScaleAnimation(1.0f, 1.0f, 0.5f, 1.0f);
            scaleAnim2.setDuration(3000); // 设置动画的播放时长
            scaleAnim2.setFillAfter(true); // 设置维持结束画面
            // 创建一个旋转动画
            Animation rotateAnim2 = new RotateAnimation(0f, -360f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim2.setDuration(3000); // 设置动画的播放时长
            rotateAnim2.setFillAfter(true); // 设置维持结束画面
            // 创建一个集合动画
            AnimationSet setAnim2 = new AnimationSet(true);
            setAnim2.addAnimation(alphaAnim2); // 给集合动画添加灰度动画
            setAnim2.addAnimation(translateAnim2); // 给集合动画添加平移动画
            setAnim2.addAnimation(scaleAnim2); // 给集合动画添加缩放动画
            setAnim2.addAnimation(rotateAnim2); // 给集合动画添加旋转动画
            setAnim2.setFillAfter(true); // 设置维持结束画面
            iv_anim_set.startAnimation(setAnim2); // 开始播放动画
        }
    }

    // 在补间动画重复播放时触发
    @Override
    public void onAnimationRepeat(Animation animation) {}

}
