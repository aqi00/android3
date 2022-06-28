package com.example.chapter11.widget;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.example.chapter11.util.Utils;

public class CustomScrollView extends ScrollView {
    private float mOffsetX, mOffsetY; // 横纵方向上的偏移
    private PointF mLastPos; // 上次落点的位置
    private int mInterval; // 与边缘线的间距阈值

    public CustomScrollView(Context context) {
        this(context, null);
    }

    public CustomScrollView(Context context, AttributeSet attr) {
        super(context, attr);
        mInterval = Utils.dip2px(context, 3);
    }

    // 在拦截触摸事件时触发
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean result;
        // 其余动作，包括手指移动、手指松开等等
        if (event.getAction() == MotionEvent.ACTION_DOWN) { // 按下手指
            mOffsetX = 0.0F;
            mOffsetY = 0.0F;
            mLastPos = new PointF(event.getX(), event.getY());
            result = super.onInterceptTouchEvent(event);
        } else {
            PointF thisPos = new PointF(event.getX(), event.getY());
            mOffsetX += Math.abs(thisPos.x - mLastPos.x); // x轴偏差
            mOffsetY += Math.abs(thisPos.y - mLastPos.y); // y轴偏差
            mLastPos = thisPos;
            if (mOffsetX < mInterval && mOffsetY < mInterval) {
                result = false; // false传给表示子控件，此时为点击事件
            } else if (mOffsetX < mOffsetY) {
                result = true; // true表示不传给子控件，此时为垂直滑动
            } else {
                result = false; // false表示传给子控件，此时为水平滑动
            }
        }
        return result;
    }
}
