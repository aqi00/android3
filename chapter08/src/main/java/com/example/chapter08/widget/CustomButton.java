package com.example.chapter08.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

import com.example.chapter08.R;

@SuppressLint("AppCompatCustomView")
public class CustomButton extends Button {
    private final static String TAG = "CustomButton";

    public CustomButton(Context context) {
        super(context);
        Log.d(TAG, "只有一个输入参数");
    }

    public CustomButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.customButtonStyle);
        Log.d(TAG, "有两个输入参数");
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        // 使defStyleAttr奏效的三个条件：
        // 1、attrs.xml增加定义风格属性（如customButtonStyle），且format值为reference
        // 2、styles.xml增加某种风格的样式定义（如CommonButton）
        // 3、AndroidManifest.xml的application节点的android:theme指定了哪个主题（如AppTheme），就在该主题内部补充风格属性与样式的对应关系，如
        // <item name="customButtonStyle">@style/CommonButton</item>
        //super(context, attrs, defStyleAttr); // 设置默认的样式属性
        // 下面不使用defStyleAttr，直接使用R.style.CommonButton定义的样式
        this(context, attrs, 0, R.style.CommonButton);
        Log.d(TAG, "有三个输入参数");
    }

    @SuppressLint("NewApi")
    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Log.d(TAG, "有四个输入参数");
    }
}
