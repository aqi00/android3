package com.example.chapter11.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.chapter11.util.Utils;

public class SingleTouchView extends View {
    private static final String TAG = "SingleTouchView";
    private Paint mPathPaint, mBeginPaint, mEndPaint; // 路径的画笔，以及起点和终点的画笔
    private Path mPath = new Path(); // 声明一个路径对象
    private PointF mLastPos, mBeginPos, mEndPos; // 路径中的上次触摸点，本次按压的起点和终点
    private int dip_17;

    public SingleTouchView(Context context) {
        this(context, null);
    }

    public SingleTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        dip_17 = Utils.dip2px(context, 17);
        initView(); // 初始化视图
    }

    // 初始化视图
    private void initView() {
        mPathPaint = new Paint(); // 创建路径的画笔
        mPathPaint.setStrokeWidth(5); // 设置画笔的线宽
        mPathPaint.setStyle(Paint.Style.STROKE); // 设置画笔的类型。STROK表示空心，FILL表示实心
        mPathPaint.setColor(Color.BLACK); // 设置画笔的颜色
        mBeginPaint = new Paint(); // 创建起点的画笔
        mBeginPaint.setColor(Color.RED); // 设置画笔的颜色
        mBeginPaint.setTextSize(dip_17); // 设置画笔的文字大小
        mEndPaint = new Paint(); // 创建终点的画笔
        mEndPaint.setColor(Color.DKGRAY); // 设置画笔的颜色
        mEndPaint.setTextSize(dip_17); // 设置画笔的文字大小
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mPath, mPathPaint); // 在画布上绘制指定路径线条
        if (mBeginPos != null) { // 存在起点，则绘制起点的实心圆及其文字
            canvas.drawCircle(mBeginPos.x, mBeginPos.y, 10, mBeginPaint);
            canvas.drawText("起点", mBeginPos.x-dip_17, mBeginPos.y+dip_17, mBeginPaint);
        }
        if (mEndPos != null) { // 存在终点，则绘制终点的实心圆及其文字
            canvas.drawCircle(mEndPos.x, mEndPos.y, 10, mEndPaint);
            canvas.drawText("终点", mEndPos.x-dip_17, mEndPos.y+dip_17, mEndPaint);
        }
    }

    // 在发生触摸事件时触发
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 按下手指
                mPath.reset();
                mPath.moveTo(event.getX(), event.getY()); // 移动到指定坐标点
                mBeginPos = new PointF(event.getX(), event.getY());
                mEndPos = null;
                break;
            case MotionEvent.ACTION_MOVE: // 移动手指
                // 连接上一个坐标点和当前坐标点
                mPath.quadTo(mLastPos.x, mLastPos.y, event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP: // 松开手指
                mEndPos = new PointF(event.getX(), event.getY());
                // 连接上一个坐标点和当前坐标点
                mPath.quadTo(mLastPos.x, mLastPos.y, event.getX(), event.getY());
                if (mListener != null) { // 触发手势飞掠动作
                    mListener.onFlipFinish(mBeginPos, mEndPos);
                }
                break;
        }
        mLastPos = new PointF(event.getX(), event.getY());
        postInvalidate(); // 立即刷新视图（线程安全方式）
        return true;
    }

    private FlipListener mListener; // 声明一个手势飞掠监听器
    public void setFlipListener(FlipListener listener) {
        mListener = listener;
    }

    // 定义一个手势飞掠的监听器接口
    public interface FlipListener {
        void onFlipFinish(PointF beginPos, PointF endPos);
    }
}
