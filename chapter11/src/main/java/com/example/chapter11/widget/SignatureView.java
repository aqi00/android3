package com.example.chapter11.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.chapter11.R;
import com.example.chapter11.bean.PathPosition;

import java.util.ArrayList;
import java.util.List;

public class SignatureView extends View {
    private static final String TAG = "SignatureView";
    private Paint mPathPaint = new Paint(); // 声明一个画笔对象
    private Path mPath = new Path(); // 声明一个路径对象
    private int mPathPaintColor = Color.BLACK; // 画笔颜色
    private int mStrokeWidth = 3; // 画笔线宽
    private PathPosition mPathPos = new PathPosition(); // 路径位置
    private List<PathPosition> mPathList = new ArrayList<>(); // 路径位置列表
    private PointF mLastPos; // 上次触摸点的横纵坐标

    public SignatureView(Context context) {
        this(context, null);
    }

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            // 根据SignatureView的属性定义，从布局文件中获取属性数组描述
            TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.SignatureView);
            // 根据属性描述定义，获取布局文件中的画笔颜色
            mPathPaintColor = attrArray.getColor(R.styleable.SignatureView_paint_color, Color.BLACK);
            // 根据属性描述定义，获取布局文件中的画笔线宽
            mStrokeWidth = attrArray.getInt(R.styleable.SignatureView_stroke_width, 3);
            attrArray.recycle(); // 回收属性数组描述
        }
        initView(); // 初始化视图
    }

    // 初始化视图
    private void initView() {
        mPathPaint.setStrokeWidth(mStrokeWidth); // 设置画笔的线宽
        mPathPaint.setStyle(Paint.Style.STROKE); // 设置画笔的类型。STROK表示空心，FILL表示实心
        mPathPaint.setColor(mPathPaintColor); // 设置画笔的颜色
        setDrawingCacheEnabled(true); // 开启当前视图的绘图缓存
    }

    // 清空画布
    public void clear() {
        mPath.reset(); // 重置路径对象
        mPathList.clear(); // 清空路径列表
        postInvalidate(); // 立即刷新视图（线程安全方式）
    }

    // 撤销上一次绘制
    public void revoke() {
        if (mPathList.size() > 0) {
            // 移除路径位置列表中的最后一个路径
            mPathList.remove(mPathList.size() - 1);
            mPath.reset(); // 重置路径对象
            for (int i = 0; i < mPathList.size(); i++) {
                PathPosition pp = mPathList.get(i);
                // 移动到上一个坐标点
                mPath.moveTo(pp.prePos.x, pp.prePos.y);
                // 连接上一个坐标点和下一个坐标点
                mPath.quadTo(pp.prePos.x, pp.prePos.y, pp.nextPos.x, pp.nextPos.y);
            }
            postInvalidate(); // 立即刷新视图（线程安全方式）
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mPath, mPathPaint); // 在画布上绘制指定路径线条
    }

    // 在发生触摸事件时触发
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 按下手指
                mPath.moveTo(event.getX(), event.getY()); // 移动到指定坐标点
                mPathPos.prePos = new PointF(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE: // 移动手指
                // 连接上一个坐标点和当前坐标点
                mPath.quadTo(mLastPos.x, mLastPos.y, event.getX(), event.getY());
                mPathPos.nextPos = new PointF(event.getX(), event.getY());
                mPathList.add(mPathPos); // 往路径位置列表添加路径位置
                mPathPos = new PathPosition(); // 创建新的路径位置
                mPathPos.prePos = new PointF(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP: // 松开手指
                // 连接上一个坐标点和当前坐标点
                mPath.quadTo(mLastPos.x, mLastPos.y, event.getX(), event.getY());
                break;
        }
        mLastPos = new PointF(event.getX(), event.getY());
        postInvalidate(); // 立即刷新视图（线程安全方式）
        return true;
    }

}
