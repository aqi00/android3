package com.example.chapter11.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class StatusBarUtil {
    private static final String TAG_MARGIN_ADDED = "marginAdded";

    // 获取顶部状态栏的高度
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = -1;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            // 根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    // 把页面内容顶到状态栏内部，看起来状态栏就像是悬浮在页面之上
    public static void fullScreen(Activity activity) {
        Window window = activity.getWindow();
        View decorView = window.getDecorView();
        // 两个标志位要结合使用，表示让应用的主体内容占用系统状态栏的空间
        // 第三个标志位可让底部导航栏变透明View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        decorView.setSystemUiVisibility(option);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // 需要把状态栏颜色设置透明，这样才有悬浮的效果
        setStatusBarColor(activity, Color.TRANSPARENT);
    }

    // 重置状态栏。即把状态栏颜色恢复为系统默认的黑色
    public static void reset(Activity activity) {
        setStatusBarColor(activity, Color.BLACK);
    }

    // 设置状态栏的背景色
    public static void setStatusBarColor(Activity activity, int color) {
        activity.getWindow().setStatusBarColor(color);
        // 底部导航栏颜色也可以由系统设置
        //activity.getWindow().setNavigationBarColor(color);
        if (color == Color.BLACK) { // 黑色背景表示要恢复状态栏
            addMarginTop(activity);
        } else { // 其他背景表示要悬浮状态栏
            removeMarginTop(activity);
        }
    }

    // 设置状态栏的背景色。对Android7.0以上版本要区分处理
    public static void setStatusBarColor(Activity activity, int color, boolean isUp) {
        int statusBarColor = color;
        if (isUp) { // 上拉页面
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                decorView.setSystemUiVisibility(option);
            } else {
                statusBarColor = 0xFFB0B0B0;
            }
        } else { // 下拉页面
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_VISIBLE;
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                decorView.setSystemUiVisibility(option);
            }
        }
        activity.getWindow().setStatusBarColor(statusBarColor);
        // 底部导航栏颜色也可以由系统设置
        //activity.getWindow().setNavigationBarColor(statusBarColor);
        if (color == Color.BLACK) { // 黑色背景表示要恢复状态栏
            addMarginTop(activity);
        } else { // 其他背景表示要悬浮状态栏
            removeMarginTop(activity);
        }
    }

    // 添加顶部间隔，留出状态栏的位置
    private static void addMarginTop(Activity activity) {
        Window window = activity.getWindow();
        ViewGroup contentView = window.findViewById(Window.ID_ANDROID_CONTENT);
        View child = contentView.getChildAt(0);
        if (!TAG_MARGIN_ADDED.equals(child.getTag())) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) child.getLayoutParams();
            // 添加的间隔大小就是状态栏的高度
            params.topMargin += getStatusBarHeight(activity);
            child.setLayoutParams(params);
            child.setTag(TAG_MARGIN_ADDED);
        }
    }

    // 移除顶部间隔，霸占状态栏的位置
    private static void removeMarginTop(Activity activity) {
        Window window = activity.getWindow();
        ViewGroup contentView = window.findViewById(Window.ID_ANDROID_CONTENT);
        View child = contentView.getChildAt(0);
        if (TAG_MARGIN_ADDED.equals(child.getTag())) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) child.getLayoutParams();
            // 移除的间隔大小就是状态栏的高度
            params.topMargin -= getStatusBarHeight(activity);
            child.setLayoutParams(params);
            child.setTag(null);
        }
    }

}
