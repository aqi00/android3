package com.example.chapter20.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;

public class CircleDrawable extends BitmapDrawable {
    private Paint mPaint = new Paint(); // 声明一个画笔对象

    public CircleDrawable(Context ctx, Bitmap bitmap) {
        super(ctx.getResources(), bitmap);
        // 创建一个位图着色器，CLAMP表示边缘拉伸
        BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
        mPaint.setShader(shader); // 设置画笔的着色器对象
    }

    @Override
    public void draw(Canvas canvas) {
        int width = getBitmap().getWidth();
        int height = getBitmap().getHeight();
        int radius = Math.min(width, height) / 2 - 4;
        // 在画布上绘制圆形，也就是只显示圆形内部的图像
        canvas.drawCircle(width/2, height/2, radius, mPaint);
    }

}
