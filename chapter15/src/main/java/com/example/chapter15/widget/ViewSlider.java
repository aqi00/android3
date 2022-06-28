package com.example.chapter15.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ClickableViewAccessibility")
public class ViewSlider extends FrameLayout {
    private final static String TAG = "ViewSlider";
    private static int DIRECTION_LEFT = -1; // 左边方向
    private static int DIRECTION_RIGHT = 1; // 右边方向
    private static int SHOW_PRE = 1; // 拉出上一个页面
    private static int SHOW_NEXT = 2; // 拉出下一个页面
    private Context mContext;
    private int mWidth; // 视图宽度
    private float mLastX = 0; // 上次按下点的横坐标
    private List<String> mPathList = new ArrayList<>(); // 图片路径列表
    private int mPos = 0; // 当前书页的序号
    private PageView mPreView, mCurrentView, mNextView; // 上一个视图、当前视图、下一个视图
    private int mShowPage; // 显示页面类型
    private int mDirection; // 滑动方向
    private Scroller mScroller; // 声明一个滚动器对象
    private boolean isScrolling = false; // 是否正在滚动

    public ViewSlider(Context context) {
        this(context, null);
    }

    public ViewSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        // 创建一个基于线性插值器的滚动器对象
        mScroller = new Scroller(context, new LinearInterpolator());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth(); // 获取视图的实际宽度
    }

    // 设置图片路径列表
    public void setFilePath(List<String> pathList) {
        removeAllViews(); // 移除所有下级视图
        mPathList = pathList;
        if (mPathList.size() > 0) {
            mCurrentView = getBookPage(0, true);
            addView(mCurrentView); // 添加当前书页视图
        }
        if (mPathList.size() > 1) {
            mNextView = getBookPage(1, false);
            addView(mNextView, 0); // 添加下一个书页视图
        }
    }

    // 获取一个书页视图
    private PageView getBookPage(int position, boolean isUp) {
        // 创建一个书页视图
        PageView page = new PageView(mContext);
        MarginLayoutParams params = new LinearLayout.LayoutParams(
                mWidth, LayoutParams.WRAP_CONTENT);
        page.setLayoutParams(params); // 设置书页视图的布局参数
        Bitmap bitmap = BitmapFactory.decodeFile(mPathList.get(position));
        int iv_height = (int)(1.0*bitmap.getHeight()/bitmap.getWidth() * mWidth);
        MarginLayoutParams iv_params = new LinearLayout.LayoutParams(mWidth, iv_height);
        ImageView iv = new ImageView(mContext);
        iv.setLayoutParams(iv_params); // 设置图像视图的布局参数
        iv.setImageBitmap(bitmap); // 设置图像视图的位图对象
        page.addView(iv); // 把图像视图添加到书页视图
        page.setUp(isUp); // 设置是否高亮显示
        return page;
    }

    // 在发生触摸事件时触发
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mScroller.isFinished() && isScrolling) { // 正在滚动则忽略触摸事件
            return super.onTouchEvent(event);
        }
        int distanceX = (int) (event.getRawX() - mLastX);
        Log.d(TAG, "action=" + event.getAction() + ", distanceX=" + distanceX);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 按下手指
                mLastX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE: // 移动手指
                if (distanceX > 0) {  // 拉出上一页
                    if (mPos != 0) {
                        mShowPage = SHOW_PRE;
                        mPreView.setUp(true); // 高亮显示上一个书页
                        mPreView.setMargin(-mWidth + distanceX); // 设置上一个书页的左侧间距
                        mCurrentView.setUp(false); // 当前书页取消高亮
                    }
                } else {  // 拉出下一页
                    if (mPos < mPathList.size() - 1) {
                        mShowPage = SHOW_NEXT;
                        mCurrentView.setMargin(distanceX); // 设置当前书页的左侧间距
                    }
                }
                break;
            case MotionEvent.ACTION_UP: // 松开手指
                if ((mPos==0 && distanceX>0) || (mPos==mPathList.size()-1 && distanceX<0)) {
                    break; // 第一页不准往前翻页，最后一页不准往后翻页
                }
                isScrolling = true;
                if (mShowPage == SHOW_PRE) { // 原来在拉出上一页
                    mDirection = Math.abs(distanceX) < mWidth / 2 ? DIRECTION_LEFT : DIRECTION_RIGHT;
                    int distance = mDirection==DIRECTION_LEFT ? -distanceX : mWidth-distanceX;
                    mScroller.startScroll(-mWidth + distanceX, 0, distance, 0, 400);
                } else if (mShowPage == SHOW_NEXT) { // 原来在拉出下一页
                    mDirection = Math.abs(distanceX) > mWidth / 2 ? DIRECTION_LEFT : DIRECTION_RIGHT;
                    int distance = mDirection==DIRECTION_RIGHT ? -distanceX : -(mWidth+distanceX);
                    mScroller.startScroll(distanceX, 0, distance, 0, 400);
                }
                Log.d(TAG, "mShowPage="+mShowPage+", mDirection="+mDirection);
                break;
        }
        return true;
    }

    // 在滚动器滑动过程中不断触发，用于计算当前的视图偏移位置
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            PageView view = mShowPage == SHOW_PRE ? mPreView : mCurrentView;
            view.setMargin(mScroller.getCurrX());
            if (mScroller.getFinalX() == mScroller.getCurrX()) {
                onScrollEnd(mDirection); // 重新规定上一页、当前页和下一页视图
                isScrolling = false;
            }
        }
    }

    // 在滚动完成后触发，重新规定上一页、当前页和下一页视图
    public void onScrollEnd(int direction) {
        if (mShowPage == SHOW_PRE) { // 原来在拉出上一页
            if (direction == DIRECTION_RIGHT) { // 往右滚动
                mPos--;
                if (mNextView != null) {
                    removeView(mNextView); // 移除下一页视图
                }
                mNextView = mCurrentView; // 之前的当前视图变成了现在的下一个视图
                mCurrentView = mPreView; // 之前的上一页视图变成了现在的当前视图
                if (mPos > 0) {
                    mPreView = getBookPage(mPos - 1, false);
                    addView(mPreView); // 添加现在的上一页视图
                    mPreView.setMargin(-mWidth);
                }
            }
            mCurrentView.setUp(true); // 高亮显示当前书页
        } else if (mShowPage == SHOW_NEXT) { // 原来在拉出下一页
            if (direction == DIRECTION_LEFT) { // 往左滚动
                mPos++;
                if (mPreView != null) {
                    removeView(mPreView); // 移除上一页视图
                }
                mPreView = mCurrentView; // 之前的当前视图变成了现在的上一个视图
                mCurrentView = mNextView; // 之前的下一页视图变成了现在的当前视图
                if (mPos < mPathList.size() - 1) {
                    mNextView = getBookPage(mPos + 1, false);
                    addView(mNextView, 0); // 添加现在的下一页视图
                }
            }
            mCurrentView.setUp(true); // 高亮显示当前书页
        }
    }

}
