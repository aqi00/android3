package com.example.chapter12.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

@SuppressLint("DrawAllocation")
public class ShutterView extends View {
    private final static String TAG = "ShutterView";
    private Paint mPaint = new Paint(); // 声明一个画笔对象
    private int mOriention = LinearLayout.HORIZONTAL; // 动画方向
    private int mLeafCount = 10; // 叶片的数量
    private PorterDuff.Mode mMode = PorterDuff.Mode.DST_IN; // 绘图模式为只展示交集
    private Bitmap mBitmap; // 声明一个位图对象
    private int mRatio = 0; // 绘制的比率

    public ShutterView(Context context) {
        this(context, null);
    }

    public ShutterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 设置百叶窗的方向
    public void setOriention(int oriention) {
        mOriention = oriention;
    }

    // 设置百叶窗的叶片数量
    public void setLeafCount(int leaf_count) {
        mLeafCount = leaf_count;
    }

    // 设置绘图模式
    public void setMode(PorterDuff.Mode mode) {
        mMode = mode;
    }

    // 设置位图对象
    public void setImageBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    // 设置绘图比率
    public void setRatio(int ratio) {
        mRatio = ratio;
        postInvalidate(); // 立即刷新视图（线程安全方式）
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap == null) {
            return;
        }
        int width = getMeasuredWidth(); // 获取视图的实际宽度
        int height = getMeasuredHeight(); // 获取视图的实际高度
        // 创建一个遮罩位图
        Bitmap mask = Bitmap.createBitmap(width, height, mBitmap.getConfig());
        Canvas canvasMask = new Canvas(mask); // 创建一个遮罩画布
        for (int i = 0; i < mLeafCount; i++) {
            if (mOriention == LinearLayout.HORIZONTAL) { // 水平方向
                int column_width = (int) Math.ceil(width * 1f / mLeafCount);
                int left = column_width * i;
                int right = left + column_width * mRatio / 100;
                // 在遮罩画布上绘制各矩形叶片
                canvasMask.drawRect(left, 0, right, height, mPaint);
            } else { // 垂直方向
                int row_height = (int) Math.ceil(height * 1f / mLeafCount);
                int top = row_height * i;
                int bottom = top + row_height * mRatio / 100;
                // 在遮罩画布上绘制各矩形叶片
                canvasMask.drawRect(0, top, width, bottom, mPaint);
            }
        }
        // 设置离屏缓存
        int saveLayer = canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        Rect rect = new Rect(0, 0, width, width * mBitmap.getHeight() / mBitmap.getWidth());
        canvas.drawBitmap(mBitmap, null, rect, mPaint); // 绘制目标图像
        mPaint.setXfermode(new PorterDuffXfermode(mMode)); // 设置混合模式
        canvas.drawBitmap(mask, 0, 0, mPaint); // 再绘制源图像的遮罩
        mPaint.setXfermode(null); // 还原混合模式
        canvas.restoreToCount(saveLayer); // 还原画布
    }

}
