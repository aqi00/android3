package com.example.chapter15.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

public class CurveView extends View {
    private final static String TAG = "CurveView";
    private static final int CLICK_TOP = 1; // 点击了上面部分
    private static final int CLICK_BOTTOM = 2; // 点击了下面部分
    private int mClickType = CLICK_BOTTOM; // 点击类型，点击了上半部分还是下半部分
    private PointF a,f,g,e,h,c,j,b,k,d,i; // 贝塞尔曲线的各个关联点坐标
    private int mViewWidth, mViewHeight; // 视图的宽度和高度
    private Scroller mScroller; // 声明一个滚动器对象

    private List<String> mPathList = new ArrayList<>(); // 图片路径列表
    private Bitmap mPreBitmap; // 上一页的位图
    private Bitmap mThisBitmap; // 当前页的位图
    private Bitmap mNextBitmap; // 下一页的位图
    private Bitmap mBlankBitmap; // 空白位图
    private boolean needMove = false; // 是否需要移动
    private boolean needChange = false; // 是否需要改变图像
    private int mCurrentPos = 0; // 当前图像的序号

    public CurveView(Context context) {
        this(context, null);
    }

    public CurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        a = new PointF(-1, -1);
        f = new PointF();
        g = new PointF();
        e = new PointF();
        h = new PointF();
        c = new PointF();
        j = new PointF();
        b = new PointF();
        k = new PointF();
        d = new PointF();
        i = new PointF();
        // 创建一个基于线性插值器的滚动器对象
        mScroller = new Scroller(context, new LinearInterpolator());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth(); // 获取视图的实际宽度
        mViewHeight = getMeasuredHeight(); // 获取视图的实际高度
        Log.d(TAG, "mViewWidth="+mViewWidth+", mViewHeight="+mViewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPathList==null || mPathList.size()==0) {
            return;
        }
        canvas.drawColor(Color.LTGRAY); // 把每页背面涂成浅灰色
        if (a.x==-1 && a.y==-1) { // 尚未翻页
            canvas.drawBitmap(mThisBitmap, null, new RectF(0, 0, mViewWidth, mViewHeight), null);
        } else { // 正在翻页
            if (f.x==mViewWidth && f.y==0) { // 上半部分翻页
                drawCurrentView(canvas, getCurrentPathTop()); // 绘制当前页
                drawNextView(canvas, getCurrentPathTop()); // 绘制下一页
            } else if (f.x==mViewWidth && f.y==mViewHeight) { // 下半部分翻页
                drawCurrentView(canvas, getCurrentPathBottom()); // 绘制当前页
                drawNextView(canvas, getCurrentPathBottom()); // 绘制下一页
            }
        }
    }

    // 在滚动器滑动过程中不断触发，计算并显示视图界面
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) { // 尚未滚动完毕
            float x = mScroller.getCurrX();
            float y = mScroller.getCurrY();
            showTouchResult(x, y, mClickType); // 显示触摸结果
            if (mScroller.getFinalX() == x && mScroller.getFinalY() == y) { // 已经滚到终点了
                if (needChange) {
                    exchangeBitmap(true); // 改变当前显示的图像
                }
                reset(); // 回到默认状态
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 按下手指
                needMove = !((x<=mViewWidth/2 && mCurrentPos==0)
                        || (x>=mViewWidth/2 && mCurrentPos==mPathList.size()));
                if (needMove) {
                    if (x < mViewWidth/2) {
                        exchangeBitmap(false); // 改变当前显示的图像
                    }
                    int clickType = (y<=mViewHeight/2) ? CLICK_TOP : CLICK_BOTTOM;
                    showTouchResult(x, y, clickType); // 显示触摸结果
                }
                break;
            case MotionEvent.ACTION_MOVE: // 移动手指
                if (needMove) {
                    showTouchResult(x, y, mClickType); // 显示触摸结果
                }
                break;
            case MotionEvent.ACTION_UP: // 松开手指
                if (needMove) {
                    needChange = x < mViewWidth / 2;
                    if (needChange) {
                        rollFront(); // 滚到上一页
                    } else {
                        rollBack(); // 滚回当前页
                    }
                }
                break;
        }
        return true;
    }

    // 设置图片路径列表
    public void setFilePath(List<String> pathList) {
        mPathList = pathList;
        Log.d(TAG, "size="+mPathList.size());
        mThisBitmap = BitmapFactory.decodeFile(mPathList.get(0));
        mBlankBitmap = Bitmap.createBitmap(mThisBitmap.getWidth(), mThisBitmap.getHeight(), Bitmap.Config.RGB_565);
        mBlankBitmap.eraseColor(Color.WHITE);
        if (mPathList.size() > 1) {
            mNextBitmap = BitmapFactory.decodeFile(mPathList.get(1));
        } else {
            mNextBitmap = mBlankBitmap;
        }
        postInvalidate(); // 立即刷新视图（线程安全方式）
    }

    // 改变当前显示的图像
    private void exchangeBitmap(boolean isPlus) {
        Log.d(TAG, "exchangeBitmap "+isPlus);
        if (isPlus) { // 翻到后一页图像
            mCurrentPos++;
            mPreBitmap = mThisBitmap;
            mThisBitmap = mNextBitmap;
            if (mCurrentPos+1 < mPathList.size()) {
                mNextBitmap = BitmapFactory.decodeFile(mPathList.get(mCurrentPos+1));
            } else {
                mNextBitmap = mBlankBitmap;
            }
        } else { // 翻到前一页图像
            mCurrentPos--;
            mNextBitmap = mThisBitmap;
            mThisBitmap = mPreBitmap;
            if (mCurrentPos-1 >= 0) {
                mPreBitmap = BitmapFactory.decodeFile(mPathList.get(mCurrentPos-1));
            }
        }
    }

    // 滚回当前页
    public void rollBack() {
        int dx, dy;
        // 让a滑动到F点所在位置，留出1像素是为了防止当a和f重叠时出现View闪烁的情况
        if (mClickType == CLICK_TOP) { // 从上半部分翻页
            dx = (int) (mViewWidth-1-a.x);
            dy = (int) (1-a.y);
        } else { // 从下半部分翻页
            dx = (int) (mViewWidth-1-a.x);
            dy = (int) (mViewHeight-1-a.y);
        }
        // 命令滚动器开始滚动操作
        mScroller.startScroll((int) a.x, (int) a.y, dx, dy, 400);
    }

    // 滚到上一页
    public void rollFront() {
        int dx, dy;
        // 让A点滑动到F点所在位置，为了防止A点和F点重叠时出现闪烁状况，需要先留出1个像素
        if (mClickType == CLICK_TOP) { // 从上半部分翻页
            dx = (int) (-1-a.x);
            dy = (int) (1-a.y);
        } else { // 从下半部分翻页
            dx = (int) (-1-a.x);
            dy = (int) (mViewHeight-1-a.y);
        }
        // 命令滚动器开始滚动操作
        mScroller.startScroll((int) a.x, (int) a.y, dx, dy, 400);
    }

    // 显示触摸结果
    private void showTouchResult(float x, float y, int clickType) {
        a = new PointF(x, y);
        mClickType = clickType;
        int fy = (mClickType == CLICK_TOP) ? 0 : mViewHeight;
        f = new PointF(mViewWidth, fy);
        calcEachPoint(a, f); // 计算各点的坐标
        PointF touchPoint = new PointF(x, y);
        if (calcPointCX(touchPoint, f)<0) { // 如果C点的x坐标小于0，就重新测量C点的坐标
            calcPointA(); // 如果C点的x坐标小于0，就根据触摸点重新测量A点的坐标
            calcEachPoint(a, f); // 计算各点的坐标
        }
        postInvalidate(); // 立即刷新视图（线程安全方式）
    }

    // 如果C点的x坐标小于0，就根据触摸点重新测量A点的坐标
    private void calcPointA() {
        float w0 = mViewWidth - c.x;
        float w1 = Math.abs(f.x - a.x);
        float w2 = mViewWidth * w1 / w0;
        float h1 = Math.abs(f.y - a.y);
        float h2 = w2 * h1 / w1;
        a = new PointF(Math.abs(f.x - w2), Math.abs(f.y - h2));
    }

    // 回到默认状态
    public void reset() {
        a = new PointF(-1, -1);
        postInvalidate(); // 立即刷新视图（线程安全方式）
    }

    // 绘制当前页
    private void drawCurrentView(Canvas canvas, Path currentPath) {
        canvas.save(); // 保存画布
        canvas.clipPath(currentPath); // 根据指定路径裁剪画布
        canvas.drawBitmap(mThisBitmap, null, new RectF(0, 0, mViewWidth, mViewHeight), null);
        canvas.restore(); // 还原画布
    }

    // 获取上面翻页时的当前页轮廓路径
    private Path getCurrentPathTop() {
        Path currentPath = new Path();
        currentPath.lineTo(c.x,c.y); // 移动到C点
        currentPath.quadTo(e.x,e.y,b.x,b.y); // 从C点到B点画贝塞尔曲线，此时控制点为E
        currentPath.lineTo(a.x,a.y); // 移动到A点
        currentPath.lineTo(k.x,k.y); // 移动到K点
        currentPath.quadTo(h.x,h.y,j.x,j.y); // 从K点到J点画贝塞尔曲线，此时控制点为H
        currentPath.lineTo(mViewWidth, mViewHeight); // 移动到右下角
        currentPath.lineTo(0, mViewHeight); // 移动到左下角
        currentPath.close(); // 闭合区域
        return currentPath;
    }

    // 获取下面翻页时的当前页轮廓路径
    private Path getCurrentPathBottom() {
        Path currentPath = new Path();
        currentPath.lineTo(0, mViewHeight); // 移动到左下角
        currentPath.lineTo(c.x,c.y); // 移动到C点
        currentPath.quadTo(e.x,e.y,b.x,b.y); // 从C点到B点画贝塞尔曲线，此时控制点为E
        currentPath.lineTo(a.x,a.y); // 移动到A点
        currentPath.lineTo(k.x,k.y); // 移动到K点
        currentPath.quadTo(h.x,h.y,j.x,j.y); // 从K点到J点画贝塞尔曲线，此时控制点为H
        currentPath.lineTo(mViewWidth,0); // 移动到右上角
        currentPath.close(); // 闭合区域
        return currentPath;
    }

    // 绘制下一页
    private void drawNextView(Canvas canvas, Path currentPath) {
        canvas.save(); // 保存画布
        Path nextPath = getNextPath(); // 获得下一页的轮廓路径
        nextPath.op(currentPath, Path.Op.DIFFERENCE); // 去除当前页的部分
        nextPath.op(getBackPath(), Path.Op.DIFFERENCE); // 去除背面页的部分
        canvas.clipPath(nextPath); // 根据指定路径裁剪画布
        canvas.drawBitmap(mNextBitmap, null, new RectF(0, 0, mViewWidth, mViewHeight), null);
        canvas.restore(); // 还原画布
    }

    // 获得下一页的轮廓路径
    private Path getNextPath() {
        Path nextPath = new Path(); // 从左上角开始
        nextPath.lineTo(0, mViewHeight); // 移动到左下角
        nextPath.lineTo(mViewWidth, mViewHeight); // 移动到右下角
        nextPath.lineTo(mViewWidth, 0); // 移动到右上角
        nextPath.close(); // 闭合区域（右上角到左上角）
        return nextPath;
    }

    // 获得背面页的轮廓路径
    private Path getBackPath() {
        Path backPath = new Path();
        backPath.moveTo(i.x,i.y); // 移动到I点
        backPath.lineTo(d.x,d.y); // 移动到D点
        backPath.lineTo(b.x,b.y); // 移动到B点
        backPath.lineTo(a.x,a.y); // 移动到A点
        backPath.lineTo(k.x,k.y); // 移动到K点
        backPath.close(); // 闭合区域
        return backPath;
    }

    // 计算各点的坐标
    private void calcEachPoint(PointF a, PointF f) {
        g.x = (a.x + f.x) / 2;
        g.y = (a.y + f.y) / 2;
        e.x = g.x - (f.y - g.y) * (f.y - g.y) / (f.x - g.x);
        e.y = f.y;
        h.x = f.x;
        h.y = g.y - (f.x - g.x) * (f.x - g.x) / (f.y - g.y);
        c.x = e.x - (f.x - e.x) / 2;
        c.y = f.y;
        j.x = f.x;
        j.y = h.y - (f.y - h.y) / 2;
        b = getCrossPoint(a,e,c,j); // 计算线段AE与CJ的交点坐标
        k = getCrossPoint(a,h,c,j); // 计算线段AH与CJ的交点坐标
        d.x = (c.x + 2 * e.x + b.x) / 4;
        d.y = (2 * e.y + c.y + b.y) / 4;
        i.x = (j.x + 2 * h.x + k.x) / 4;
        i.y = (2 * h.y + j.y + k.y) / 4;
    }

    // 计算两条线段的交点坐标
    private PointF getCrossPoint(PointF firstP1, PointF firstP2, PointF secondP1, PointF secondP2) {
        float dxFirst = firstP1.x - firstP2.x, dyFirst = firstP1.y - firstP2.y;
        float dxSecond = secondP1.x - secondP2.x, dySecond = secondP1.y - secondP2.y;
        float gapCross = dxSecond*dyFirst - dxFirst*dySecond;
        float firstCross = firstP1.x * firstP2.y - firstP2.x * firstP1.y;
        float secondCross = secondP1.x * secondP2.y - secondP2.x * secondP1.y;
        float pointX = (dxFirst*secondCross - dxSecond*firstCross) / gapCross;
        float pointY = (dyFirst*secondCross - dySecond*firstCross) / gapCross;
        return new PointF(pointX, pointY);
    }

    // 计算C点的x坐标
    private float calcPointCX(PointF a, PointF f) {
        PointF g = new PointF((a.x + f.x) / 2, (a.y + f.y) / 2);
        PointF e = new PointF(g.x - (f.y - g.y) * (f.y - g.y) / (f.x - g.x), f.y);
        return e.x - (f.x - e.x) / 2;
    }
}
