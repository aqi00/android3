package com.example.chapter08.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class DrawRelativeLayout extends RelativeLayout {
    private int mDrawType = 0; // 绘制类型
    private Paint mPaint = new Paint(); // 创建一个画笔对象
    private int mStrokeWidth = 3; // 线宽

    public DrawRelativeLayout(Context context) {
        this(context, null);
    }

    public DrawRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setAntiAlias(true); // 设置画笔为无锯齿
        mPaint.setDither(true); // 设置画笔为防抖动
        mPaint.setColor(Color.BLACK); // 设置画笔的颜色
        mPaint.setStrokeWidth(mStrokeWidth); // 设置画笔的线宽
        mPaint.setStyle(Style.STROKE); // 设置画笔的类型。STROKE表示空心，FILL表示实心
    }

    // onDraw方法在绘制下级视图之前调用
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth(); // 获得布局的实际宽度
        int height = getMeasuredHeight(); // 获得布局的实际高度
        if (width > 0 && height > 0) {
            if (mDrawType == 1) { // 绘制矩形
                Rect rect = new Rect(0, 0, width, height);
                canvas.drawRect(rect, mPaint); // 在画布上绘制矩形
            } else if (mDrawType == 2) { // 绘制圆角矩形
                RectF rectF = new RectF(0, 0, width, height);
                canvas.drawRoundRect(rectF, 30, 30, mPaint); // 在画布上绘制圆角矩形
            } else if (mDrawType == 3) { // 绘制圆圈
                int radius = Math.min(width, height) / 2 - mStrokeWidth;
                canvas.drawCircle(width / 2, height / 2, radius, mPaint); // 在画布上绘制圆圈
            } else if (mDrawType == 4) { // 绘制椭圆
                RectF oval = new RectF(0, 0, width, height);
                canvas.drawOval(oval, mPaint); // 在画布上绘制椭圆
            } else if (mDrawType == 5) { // 绘制矩形及其对角线
                Rect rect = new Rect(0, 0, width, height);
                canvas.drawRect(rect, mPaint); // 绘制矩形
                canvas.drawLine(0, 0, width, height, mPaint); // 绘制左上角到右下角的线段
                canvas.drawLine(0, height, width, 0, mPaint); // 绘制左下角到右上角的线段
            }
        }
    }

    // dispatchDraw方法在绘制下级视图之前调用
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int width = getMeasuredWidth(); // 获得布局的实际宽度
        int height = getMeasuredHeight(); // 获得布局的实际高度
        if (width > 0 && height > 0) {
            if (mDrawType == 6) { // 绘制矩形及其对角线
                Rect rect = new Rect(0, 0, width, height);
                canvas.drawRect(rect, mPaint); // 绘制矩形
                canvas.drawLine(0, 0, width, height, mPaint); // 绘制左上角到右下角的线段
                canvas.drawLine(0, height, width, 0, mPaint); // 绘制左下角到右上角的线段
            }
        }
    }

    // 设置绘制类型
    public void setDrawType(int type) {
        setBackgroundColor(Color.WHITE); // 背景置为白色，目的是把画布擦干净
        mDrawType = type;
        invalidate(); // 立即重新绘图，此时会触发onDraw方法和dispatchDraw方法
    }
}
