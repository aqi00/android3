package com.example.chapter12.widget;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.chapter12.R;
import com.example.chapter12.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RewardView extends RelativeLayout{
    private final static String TAG = "RewardView";
    private Context mContext; // 声明一个上下文对象
    private int mLayoutWidth, mLayoutHeight; // 声明当前视图的宽度和高度
    private LayoutParams mLayoutParams; // 声明打赏礼物的布局参数
    private List<Drawable> mDrawableList = new ArrayList<>(); // 打赏礼物的图形列表
    private int dip_35;
    private int[] mDrawableArray = new int[] {R.drawable.gift01, R.drawable.gift02,
            R.drawable.gift03, R.drawable.gift04, R.drawable.gift05, R.drawable.gift06};

    public RewardView(Context context) {
        this(context, null);
    }

    public RewardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        for (int drawableId : mDrawableArray) {
            mDrawableList.add(mContext.getDrawable(drawableId));
        }
        dip_35 = Utils.dip2px(mContext, 35);
        mLayoutParams = new LayoutParams(dip_35, dip_35);
        // 代码设置礼物的起始布局方式，底部居中
        mLayoutParams.addRule(CENTER_HORIZONTAL, TRUE);
        mLayoutParams.addRule(ALIGN_PARENT_BOTTOM, TRUE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mLayoutWidth = getMeasuredWidth(); // 获取视图的实际宽度
        mLayoutHeight = getMeasuredHeight(); // 获取视图的实际高度
    }

    // 添加打赏礼物的视图并播放打赏动画
    public void addGiftView(){
        int pos = new Random().nextInt(mDrawableList.size());
        ImageView imageView = new ImageView(mContext);
        imageView.setImageDrawable(mDrawableList.get(pos)); // 设置图像视图的图像图形
        imageView.setLayoutParams(mLayoutParams); // 设置图像视图的布局参数
        addView(imageView); // 添加打赏礼物的图像视图
        // 创建礼物的缩放动画（补间动画方式）
        ScaleAnimation scaleAnim = new ScaleAnimation(0.2f, 1.0f, 0.2f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF, 1.0f);
        scaleAnim.setDuration(500); // 设置动画的播放时长
        imageView.startAnimation(scaleAnim); // 启动礼物的缩放动画
        playBezierAnimation(imageView); // 播放礼物的漂移动画（贝塞尔曲线方式）
    }

    // 播放礼物的漂移动画（贝塞尔曲线方式）
    private void playBezierAnimation(View giftView) {
        // 初始化一个贝塞尔计算器
        BezierEvaluator evaluator = new BezierEvaluator(getPoint(), getPoint());
        PointF beginPoint = new PointF(mLayoutWidth/2 - dip_35/2, mLayoutHeight - dip_35/2);
        float endX = (float) (Math.random()*mLayoutWidth - dip_35/2);
        float endY = (float) (Math.random()*10);
        PointF endPoint = new PointF(endX, endY);
        // 创建一个属性动画
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, beginPoint, endPoint);
        // 添加属性动画的刷新监听器
        animator.addUpdateListener(animation -> {
            // 获取二阶贝塞尔曲线的坐标点，用于指定打赏礼物的当前位置
            PointF point = (PointF) animation.getAnimatedValue();
            giftView.setX(point.x); // 设置视图的横坐标
            giftView.setY(point.y); // 设置视图的纵坐标
            giftView.setAlpha(1 - animation.getAnimatedFraction()); // 设置渐变动画
        });
        animator.setTarget(giftView); // 设置动画的播放目标
        animator.setDuration(3000); // 设置动画的播放时长
        animator.start(); // 播放礼物的漂移动画
    }

    // 生成随机控制点
    private PointF getPoint() {
        PointF point = new PointF();
        point.x = (float) (Math.random()*mLayoutWidth - dip_35/2);
        point.y = (float) (Math.random()*mLayoutHeight/5);
        Log.d(TAG, "point.x="+point.x+", point.y="+point.y);
        return point;
    }

    // 贝塞尔估值器，根据输入的两个坐标点，计算二阶贝塞尔曲线上的对应坐标
    public static class BezierEvaluator implements TypeEvaluator<PointF> {
        private PointF mPoint1, mPoint2;
        
        public BezierEvaluator(PointF point1, PointF point2){
            mPoint1 = point1;
            mPoint2 = point2;
        }

        @Override
        public PointF evaluate(float time, PointF startValue, PointF endValue) {
            float leftTime = 1 - time;
            PointF point = new PointF();
            point.x = leftTime * leftTime * leftTime * (startValue.x)
                    + 3 * leftTime * leftTime * time * (mPoint1.x)
                    + 3 * leftTime * time * time * (mPoint2.x)
                    + time * time * time * (endValue.x);
            point.y = leftTime * leftTime * leftTime * (startValue.y)
                    + 3 * leftTime * leftTime * time * (mPoint1.y)
                    + 3 * leftTime * time * time * (mPoint2.y)
                    + time * time * time * (endValue.y);
            return point;
        }
    }

}
