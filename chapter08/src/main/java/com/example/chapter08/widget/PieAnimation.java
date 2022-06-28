package com.example.chapter08.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("DrawAllocation")
public class PieAnimation extends View {
    private Paint mPaint = new Paint(); // 创建一个画笔对象
    private int mDrawingAngle = 0; // 当前绘制的角度
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private boolean isRunning = false; // 是否正在播放动画

    public PieAnimation(Context context) {
        this(context, null);
    }

    public PieAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setColor(Color.GREEN); // 设置画笔的颜色
    }

    // 开始播放动画
    public void start() {
        mDrawingAngle = 0; // 绘制角度清零
        isRunning = true;
        mHandler.post(mRefresh); // 立即启动绘图刷新任务
    }

    // 是否正在播放
    public boolean isRunning() {
        return isRunning;
    }

    // 定义一个绘图刷新任务
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            mDrawingAngle += 3; // 每次绘制时角度增加三度
            if (mDrawingAngle <= 270) { // 未绘制完成，最大绘制到270度
                invalidate(); // 立即刷新视图
                mHandler.postDelayed(this, 70); // 延迟若干时间后再次启动刷新任务
            } else { // 已绘制完成
                isRunning = false;
            }
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isRunning) { // 正在播放饼图动画
            int width = getMeasuredWidth(); // 获得已测量的宽度
            int height = getMeasuredHeight(); // 获得已测量的高度
            int diameter = Math.min(width, height); // 视图的宽高取较小的那个作为扇形的直径
            // 创建扇形的矩形边界
            RectF rectf = new RectF((width - diameter) / 2, (height - diameter) / 2,
                    (width + diameter) / 2, (height + diameter) / 2);
            // 在画布上绘制指定角度的图形。第四个参数为true绘制扇形，为false绘制圆弧
            canvas.drawArc(rectf, 0, mDrawingAngle, true, mPaint);
        }
    }

}
