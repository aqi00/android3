package com.example.chapter11.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.chapter11.util.Utils;

public class MultiTouchView extends View {
    private static final String TAG = "MultiTouchView";
    private Paint mPathPaint, mBeginPaint, mEndPaint; // 路径的画笔、虚线的画笔，以及起点和终点的画笔
    private Path mFirstPath = new Path(); // 声明主要动作的路径对象
    private Path mSecondPath = new Path(); // 声明次要动作的路径对象
    private PointF mFirstLastP, mFirstBeginP, mFirstEndP; // 主要动作的上次触摸点，本次按压的起点和终点
    private PointF mSecondLastP, mSecondBeginP, mSecondEndP; // 次要动作的上次触摸点，本次按压的起点和终点
    private boolean isFinish = false; // 是否结束触摸
    private int dip_10, dip_5;

    public MultiTouchView(Context context) {
        this(context, null);
    }

    public MultiTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        dip_10 = Utils.dip2px(context, 10);
        dip_5 = Utils.dip2px(context, 5);
        initView(); // 初始化视图
    }

    // 初始化视图
    private void initView() {
        mPathPaint = new Paint(); // 创建路径的画笔
        mPathPaint.setStrokeWidth(5); // 设置画笔的线宽
        mPathPaint.setStyle(Paint.Style.STROKE); // 设置画笔的类型。STROK表示空心，FILL表示实心
        mPathPaint.setColor(Color.BLACK); // 设置画笔的颜色
        PathEffect thinDash = new DashPathEffect(new float[]{dip_10,dip_10},1);
        mBeginPaint = new Paint(); // 创建起点连线的画笔
        mBeginPaint.setStrokeWidth(3); // 设置画笔的线宽
        mBeginPaint.setStyle(Paint.Style.STROKE); // 设置画笔的类型。STROK表示空心，FILL表示实心
        mBeginPaint.setColor(Color.RED); // 设置画笔的颜色
        mBeginPaint.setPathEffect(thinDash); // 设置虚线的样式
        PathEffect denseDash = new DashPathEffect(new float[]{dip_5,dip_5},1);
        mEndPaint = new Paint(); // 创建终点连线的画笔
        mEndPaint.setStrokeWidth(3); // 设置画笔的线宽
        mEndPaint.setStyle(Paint.Style.STROKE); // 设置画笔的类型。STROK表示空心，FILL表示实心
        mEndPaint.setColor(Color.GREEN); // 设置画笔的颜色
        mEndPaint.setPathEffect(denseDash); // 设置虚线的样式
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mFirstPath, mPathPaint); // 在画布上绘制指定路径线条
        canvas.drawPath(mSecondPath, mPathPaint); // 在画布上绘制指定路径线条
        if (isFinish) { // 结束触摸，则绘制两个起点的连线，以及两个终点的连线
            if (mFirstBeginP!=null && mSecondBeginP!=null) { // 绘制两个起点的连线
                canvas.drawLine(mFirstBeginP.x, mFirstBeginP.y, mSecondBeginP.x, mSecondBeginP.y, mBeginPaint);
            }
            if (mFirstEndP!=null && mSecondEndP!=null) { // 绘制两个终点的连线
                canvas.drawLine(mFirstEndP.x, mFirstEndP.y, mSecondEndP.x, mSecondEndP.y, mEndPaint);
            }
        }
    }

    // 在发生触摸事件时触发
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF firstP = new PointF(event.getX(), event.getY());
        PointF secondP = null;
        if (event.getPointerCount() >= 2) { // 存在多点触摸
            secondP = new PointF(event.getX(1), event.getY(1));
        }
        // 获得包括次要点在内的触摸行为
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        if (action == MotionEvent.ACTION_DOWN) { // 主要点按下
            isFinish = false;
            mFirstPath.reset();
            mSecondPath.reset();
            mFirstPath.moveTo(firstP.x, firstP.y); // 移动到指定坐标点
            mFirstBeginP = new PointF(firstP.x, firstP.y);
            mFirstEndP = null;
        } else if (action == MotionEvent.ACTION_MOVE) { // 移动手指
            if (!isFinish) {
                // 连接上一个坐标点和当前坐标点
                mFirstPath.quadTo(mFirstLastP.x, mFirstLastP.y, firstP.x, firstP.y);
                if (secondP != null) {
                    // 连接上一个坐标点和当前坐标点
                    mSecondPath.quadTo(mSecondLastP.x, mSecondLastP.y, secondP.x, secondP.y);
                }
            }
        } else if (action == MotionEvent.ACTION_UP) { // 主要点松开
        } else if (action == MotionEvent.ACTION_POINTER_DOWN) { // 次要点按下
            mSecondPath.moveTo(secondP.x, secondP.y); // 移动到指定坐标点
            mSecondBeginP = new PointF(secondP.x, secondP.y);
            mSecondEndP = null;
        } else if (action == MotionEvent.ACTION_POINTER_UP) { // 次要点松开
            isFinish = true;
            mFirstEndP = new PointF(firstP.x, firstP.y);
            mSecondEndP = new PointF(secondP.x, secondP.y);
            if (mListener != null) { // 触发手势滑动动作
                mListener.onSlideFinish(mFirstBeginP, mFirstEndP, mSecondBeginP, mSecondEndP);
            }
        }
        mFirstLastP = new PointF(firstP.x, firstP.y);
        if (secondP != null) {
            mSecondLastP = new PointF(secondP.x, secondP.y);
        }
        postInvalidate(); // 立即刷新视图（线程安全方式）
        return true;
    }

    private SlideListener mListener; // 声明一个手势滑动监听器
    public void setSlideListener(SlideListener listener) {
        mListener = listener;
    }

    // 定义一个手势滑动的监听器接口
    public interface SlideListener {
        void onSlideFinish(PointF firstBeginP, PointF firstEndP, PointF secondBeginP, PointF secondEndP);
    }
}
