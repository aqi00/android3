package com.example.chapter15.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.chapter15.util.Utils;

public class WaveView extends View {
    private Paint mPaint = new Paint(); // 声明一个画笔对象
    private Path mPath = new Path(); // 声明一个路径对象
    private int mItemWaveLength; // 一个波浪长，相当于两个二次贝塞尔曲线的长度
    private int mOriginY; // 波浪起点在纵轴上的坐标
    private int mRange; // 波浪幅度
    private int mOffsetX; // 横坐标的偏移
    private ValueAnimator mAnimator; // 声明一个属性动画对象

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setColor(Color.BLUE); // 设置画笔的颜色
        mItemWaveLength = Utils.dip2px(context, 150);
        mOriginY = Utils.dip2px(context, 60);
        mRange = Utils.dip2px(context, 50);
        initAnimator(); // 初始化属性动画
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset(); // 重置路径对象
        int halfWaveLen = mItemWaveLength / 2; // 半个波长
        mPath.moveTo(-mItemWaveLength + mOffsetX, mOriginY);  // 移动到波浪起点
        // 下面勾勒出一段连绵起伏的波浪曲线
        for (int i = -mItemWaveLength; i <= getWidth() + mItemWaveLength; i += mItemWaveLength) {
            mPath.rQuadTo(halfWaveLen / 2, -mRange, halfWaveLen, 0);
            mPath.rQuadTo(halfWaveLen / 2, mRange, halfWaveLen, 0);
        }
        mPath.lineTo(getWidth(), getHeight()); // 移动到右下角
        mPath.lineTo(0, getHeight()); // 移动到左下角
        mPath.close(); // 闭合区域
        canvas.drawPath(mPath, mPaint); // 在画布上绘制指定路径线条
    }

    // 初始化属性动画
    private void initAnimator() {
        mAnimator = ValueAnimator.ofInt(0, mItemWaveLength);
        mAnimator.setDuration(5000); // 设置属性动画的持续时间
        mAnimator.setRepeatCount(ValueAnimator.INFINITE); // 设置属性动画的重播次数。INFINITE表示持续重播
        mAnimator.setInterpolator(new LinearInterpolator()); // 设置属性动画的插值器
        // 添加属性动画的刷新监听器
        mAnimator.addUpdateListener(animation -> {
            mOffsetX = (int) animation.getAnimatedValue();
            postInvalidate(); // 立即刷新视图（线程安全方式）
        });
    }

    // 开始播放动画
    public void startAnim() {
        if (!mAnimator.isStarted()) {
            mAnimator.start();
        } else {
            mAnimator.resume();
        }
    }

    // 停止播放动画
    public void stopAnim() {
        mAnimator.pause();
    }

}
