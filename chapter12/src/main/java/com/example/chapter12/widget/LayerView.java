package com.example.chapter12.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("DrawAllocation")
public class LayerView extends View {
    private final static String TAG = "LayerView";
    private Paint mUpPaint = new Paint(); // 声明上层的画笔对象
    private Paint mDownPaint = new Paint(); // 声明下层的画笔对象
    private Paint mMaskPaint = new Paint(); // 声明遮罩的画笔对象
    private boolean onlyLine = true; // 是否只绘制轮廓
    private PorterDuff.Mode mMode; // 绘图模式

    public LayerView(Context context) {
        this(context, null);
    }

    public LayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mUpPaint.setStrokeWidth(5); // 设置画笔的线宽
        mUpPaint.setColor(Color.CYAN); // 设置画笔的颜色
        mDownPaint.setStrokeWidth(5); // 设置画笔的线宽
        mDownPaint.setColor(Color.RED); // 设置画笔的颜色
    }

    // 设置绘图模式
    public void setMode(PorterDuff.Mode mode) {
        mMode = mode;
        onlyLine = false;
        mUpPaint.setStyle(Paint.Style.FILL); // 设置画笔的类型
        mDownPaint.setStyle(Paint.Style.FILL); // 设置画笔的类型
        postInvalidate(); // 立即刷新视图（线程安全方式）
    }

    // 只显示线条轮廓
    public void setOnlyLine() {
        onlyLine = true;
        mUpPaint.setStyle(Paint.Style.STROKE); // 设置画笔的类型
        mDownPaint.setStyle(Paint.Style.STROKE); // 设置画笔的类型
        postInvalidate(); // 立即刷新视图（线程安全方式）
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getMeasuredWidth(); // 获取视图的实际宽度
        int height = getMeasuredHeight(); // 获取视图的实际高度
        if (onlyLine) { // 只绘制轮廓
            canvas.drawRect(width/3, height/3, width*9/10, height*9/10, mUpPaint);
            canvas.drawCircle(width/3, height/3, height/3, mDownPaint);
        } else if (mMode != null) { // 绘制混合后的图像
            // 创建一个遮罩位图
            Bitmap mask = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvasMask = new Canvas(mask); // 创建一个遮罩画布
            // 先绘制上层的矩形
            canvasMask.drawRect(width/3, height/3, width*9/10, height*9/10, mUpPaint);
            // 设置离屏缓存
            int saveLayer = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
            // 再绘制下层的圆形
            canvas.drawCircle(width/3, height/3, height/3, mDownPaint);
            mMaskPaint.setXfermode(new PorterDuffXfermode(mMode)); // 设置混合模式
            canvas.drawBitmap(mask, 0, 0, mMaskPaint); // 绘制源图像的遮罩
            mMaskPaint.setXfermode(null); // 还原混合模式
            canvas.restoreToCount(saveLayer); // 还原画布
        }
    }
}
