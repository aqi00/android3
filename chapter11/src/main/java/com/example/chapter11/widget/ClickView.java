package com.example.chapter11.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.chapter11.util.Utils;

public class ClickView extends View {
    private static final String TAG = "ClickView";
    private Paint mPaint = new Paint(); // 声明一个画笔对象
    private long mLastTime; // 上次按下手指的系统时间
    private PointF mPos; // 按下手指的坐标点
    private float mPressure=0; // 按压的压力值
    private int dip_10;

    public ClickView(Context context) {
        this(context, null);
    }

    public ClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        dip_10 = Utils.dip2px(context, 10);
        mPaint.setColor(Color.DKGRAY); // 设置画笔的颜色
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mPos != null) {
            // 以按压点为圆心，压力值为半径，在画布上绘制实心圆
            canvas.drawCircle(mPos.x, mPos.y, dip_10*mPressure, mPaint);
        }
    }

    // 在发生触摸事件时触发
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_DOWN
                || (event.getPressure()>mPressure)) {
            mPos = new PointF(event.getX(), event.getY());
            mPressure = event.getPressure(); // 获取本次触摸过程的最大压力值
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 按下手指
                mLastTime = event.getEventTime();
                break;
            case MotionEvent.ACTION_MOVE: // 移动手指
                break;
            case MotionEvent.ACTION_UP: // 松开手指
                if (mListener != null) { // 触发手势抬起事件
                    mListener.onLift(event.getEventTime()-mLastTime, mPressure);
                }
                break;
        }
        postInvalidate(); // 立即刷新视图（线程安全方式）
        return true;
    }

    private LiftListener mListener; // 声明一个手势抬起监听器
    public void setLiftListener(LiftListener listener) {
        mListener = listener;
    }

    // 定义一个手势抬起的监听器接口
    public interface LiftListener {
        void onLift(long time_interval, float pressure);
    }
}
