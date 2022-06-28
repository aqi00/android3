package com.example.chapter15.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.FrameLayout;

public class PageView extends FrameLayout {
    private final static String TAG = "PageView";
    private boolean isUpToTop = false; // 是否高亮显示

    public PageView(Context context) {
        super(context);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isUpToTop) { // 已经是最上面一页
            // 给画布涂上透明颜色，也就是去掉遮罩
            canvas.drawColor(Color.TRANSPARENT);
        } else { // 不是最上面一页
            // 给画布涂上半透明颜色，也就是加上遮罩
            canvas.drawColor(0x55000000);
        }
    }

    // 设置是否高亮显示
    public void setUp(boolean isUp) {
        isUpToTop = isUp;
        postInvalidate(); // 立即刷新视图（线程安全方式）
    }

    // 设置视图的左侧间距
    public void setMargin(int margin) {
        // 获取空白边缘的布局参数
        MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
        params.leftMargin = margin;
        setLayoutParams(params); // 设置视图的布局参数
        postInvalidate(); // 立即刷新视图（线程安全方式）
    }

}
