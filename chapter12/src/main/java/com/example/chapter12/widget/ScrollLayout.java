package com.example.chapter12.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.example.chapter12.R;

public class ScrollLayout extends LinearLayout {
    private final static String TAG = "ScrollLayout";
    private Scroller mScroller; // 声明一个滚动器对象
    private PointF mOriginPos; // 按下手指时候的起始点坐标
    private int mLastMargin = 0; // 上次的空白间隔
    private ImageView iv_scene; // 声明一个图像视图对象
    private Bitmap mBitmap; // 声明一个位图对象
    private boolean isScrolling = false; // 是否正在滚动

    public ScrollLayout(Context context) {
        this(context, null);
    }

    public ScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 创建一个基于线性插值器的滚动器对象
        mScroller = new Scroller(context, new LinearInterpolator());
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bj06);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        iv_scene = new ImageView(context);
        iv_scene.setLayoutParams(params); // 设置图像视图的布局参数
        iv_scene.setImageBitmap(mBitmap); // 设置图像视图的位图对象
        addView(iv_scene); // 把演示图像添加到当前视图之上
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWidth = getMeasuredWidth(); // 获取视图的实际宽度
        int ivHeight = viewWidth * mBitmap.getHeight() / mBitmap.getWidth();
        LinearLayout.LayoutParams params = (LayoutParams) iv_scene.getLayoutParams();
        params.height = ivHeight; // 根据位图的尺寸，调整图像视图的高度
        iv_scene.setLayoutParams(params);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mScroller.isFinished() && isScrolling) { // 正在滚动则忽略触摸事件
            return super.onTouchEvent(event);
        }
        PointF nowPos = new PointF(event.getX(), event.getY());
        if (event.getAction() == MotionEvent.ACTION_DOWN) { // 按下手指
            mOriginPos = new PointF(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) { // 移动手指
            moveView(mOriginPos, nowPos); // 把视图从起点移到终点
        } else if (event.getAction() == MotionEvent.ACTION_UP) { // 松开手指
            if (moveView(mOriginPos, nowPos)) { // 需要继续滚动
                isScrolling = true;
                judgeScroll(mOriginPos, nowPos); // 判断滚动方向，并发出滚动命令
            }
        }
        return true;
    }

    // 把视图从起点移到终点
    private boolean moveView(PointF lastPos, PointF thisPos) {
        int offsetX = (int) (thisPos.x-lastPos.x);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) iv_scene.getLayoutParams();
        params.leftMargin = mLastMargin + offsetX;
        params.rightMargin = -mLastMargin - offsetX;
        if (Math.abs(params.leftMargin) < iv_scene.getMeasuredWidth()) { // 还没滚到底，继续滚动
            iv_scene.setLayoutParams(params); // 设置图像视图的布局参数
            iv_scene.postInvalidate(); // 立即刷新视图（线程安全方式）
            return true;
        } else { // 已经滚到底了，停止滚动
            return false;
        }
    }

    // 判断滚动方向，并发出滚动命令
    private void judgeScroll(PointF lastPos, PointF thisPos) {
        int offsetX = (int) (thisPos.x-lastPos.x);
        if (Math.abs(offsetX) < iv_scene.getMeasuredWidth()/2) { // 滚回原处
            mScroller.startScroll(offsetX, 0, -offsetX, 0, 1000);
        } else if (offsetX >= iv_scene.getMeasuredWidth()/2) { // 滚到右边
            mScroller.startScroll(offsetX, 0, iv_scene.getMeasuredWidth()-offsetX, 0, 1000);
        } else if (offsetX <= -iv_scene.getMeasuredWidth()/2) { // 滚到左边
            mScroller.startScroll(offsetX, 0, -iv_scene.getMeasuredWidth()-offsetX, 0, 1000);
        }
    }

    // 在滚动器滑动过程中不断触发，用于计算当前的视图偏移位置
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset() && isScrolling) { // 尚未滚动完毕
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) iv_scene.getLayoutParams();
            params.leftMargin = mLastMargin + mScroller.getCurrX();
            params.rightMargin = -mLastMargin - mScroller.getCurrX();
            iv_scene.setLayoutParams(params); // 设置图像视图的布局参数
            if (mScroller.getFinalX() == mScroller.getCurrX()) { // 已经滚到终点了
                isScrolling = false;
                mLastMargin = params.leftMargin;
            }
        }
    }

}
