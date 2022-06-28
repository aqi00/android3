package com.example.chapter08.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import androidx.viewpager.widget.PagerTabStrip;

import com.example.chapter08.R;
import com.example.chapter08.util.Utils;

public class CustomPagerTab extends PagerTabStrip {
    private final static String TAG = "CustomPagerTab";
    private int textColor = Color.BLACK; // 文本颜色
    private int textSize = 15; // 文本大小

    public CustomPagerTab(Context context) {
        super(context);
    }

    public CustomPagerTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            // 根据CustomPagerTab的属性定义，从XML文件中获取属性数组描述
            TypedArray attrArray = context.obtainStyledAttributes(attrs, R.styleable.CustomPagerTab);
            // 根据属性描述定义，获取XML文件中的文本颜色
            textColor = attrArray.getColor(R.styleable.CustomPagerTab_textColor, textColor);
            // 根据属性描述定义，获取XML文件中的文本大小
            // getDimension得到的是px值，需要转换为sp值
            textSize = Utils.px2sp(context, attrArray.getDimension(
                    R.styleable.CustomPagerTab_textSize, textSize));
            Log.d(TAG, "origin textSize="+attrArray.getDimension(
                    R.styleable.CustomPagerTab_textSize, textSize));
            Log.d(TAG, "textColor=" + textColor + ", textSize=" + textSize);
            attrArray.recycle(); // 回收属性数组描述
        }
    }

//    //PagerTabStrip没有三个参数的构造方法
//    public CustomPagerTab(Context context, AttributeSet attrs, int defStyleAttr) {
//    }

    @Override
    protected void onDraw(Canvas canvas) { // 绘制方法
        super.onDraw(canvas);
        setTextColor(textColor); // 设置标题文字的文本颜色
        setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize); // 设置标题文字的文本大小
    }

}
