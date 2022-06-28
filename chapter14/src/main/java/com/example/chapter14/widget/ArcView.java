package com.example.chapter14.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.chapter14.R;
import com.example.chapter14.util.Utils;

public class ArcView extends View {
    private final static String TAG = "ArcView";
    private Paint mPaint = new Paint();
    private int mAngle = 0; // 角度
    private int mLineWidth; // 线框

    public ArcView(Context context) {
        this(context, null);
    }

    public ArcView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mLineWidth = Utils.dip2px(context, 5);
        mPaint.setColor(context.getResources().getColor(R.color.rose));
        mPaint.setStrokeWidth(mLineWidth); // 设置画笔的线宽
        mPaint.setStyle(Style.STROKE); // 设置画笔的类型。STROK表示空心，FILL表示实心
        mPaint.setAntiAlias(true); // 设置抗锯齿
        mPaint.setStrokeCap(Paint.Cap.ROUND); // 设置线头的类型。ROUND表示圆线头
    }

    public void setAngle(int angle) {
        mAngle = angle;
        postInvalidate(); // 立即刷新视图（线程安全方式）
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mAngle > 0) {
            int pad = mLineWidth/2;
            RectF rect = new RectF(pad, pad, getMeasuredWidth()-pad, getMeasuredHeight()-pad);
            // 绘制圆弧。第四个参数为true时表示绘制扇形
            canvas.drawArc(rect, -90, mAngle, false, mPaint);
        }
    }
}
