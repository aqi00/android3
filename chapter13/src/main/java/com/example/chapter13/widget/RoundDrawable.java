package com.example.chapter13.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;

import com.example.chapter13.util.Utils;

public class RoundDrawable extends BitmapDrawable {
    private Paint mPaint = new Paint(); // 声明一个画笔对象
    private int mRoundRadius; // 圆角的半径

    public RoundDrawable(Context ctx, Bitmap bitmap) {
        super(ctx.getResources(), bitmap);
        // 创建一个位图着色器，CLAMP表示边缘拉伸
        BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
        mPaint.setShader(shader); // 设置画笔的着色器对象
        mRoundRadius = Utils.dip2px(ctx, 8);
    }

    @Override
    public void draw(Canvas canvas) {
        RectF rect = new RectF(0, 0, getBitmap().getWidth(), getBitmap().getHeight());
        // 在画布上绘制圆角矩形，也就是只显示圆角矩形内部的图像
        canvas.drawRoundRect(rect, mRoundRadius, mRoundRadius, mPaint);
    }

}
