package com.example.chapter08.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class OvalView extends View {
    private Paint mPaint = new Paint(); // 创建一个画笔对象
    private int mDrawingAngle = 0; // 当前绘制的角度

    public OvalView(Context context) {
        this(context, null);
    }

    public OvalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setColor(Color.RED); // 设置画笔的颜色
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDrawingAngle += 30; // 绘制角度增加30度
        int width = getMeasuredWidth(); // 获得布局的实际宽度
        int height = getMeasuredHeight(); // 获得布局的实际高度
        RectF rectf = new RectF(0, 0, width, height); // 创建扇形的矩形边界
        // 在画布上绘制指定角度的扇形。第四个参数为true表示绘制扇形，为false表示绘制圆弧
        canvas.drawArc(rectf, 0, mDrawingAngle, true, mPaint);
    }

}
