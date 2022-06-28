package com.example.chapter12.util;

import android.content.Context;

public class Utils {
    // 根据手机的分辨率从 dp 的单位 转成为 px(像素)
    public static int dip2px(Context context, float dpValue) {
        // 获取当前手机的像素密度
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f); // 四舍五入取整
    }

    // 根据手机的分辨率从 px(像素) 的单位 转成为 dp
    public static int px2dip(Context context, float pxValue) {
        // 获取当前手机的像素密度
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f); // 四舍五入取整
    }

    // 获得屏幕的宽度
    public static int getScreenWidth(Context ctx) {
        return ctx.getResources().getDisplayMetrics().widthPixels;
//        int screenWidth;
//        // 从系统服务中获取窗口管理器
//        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            // 获取当前屏幕的四周边界
//            Rect rect = wm.getCurrentWindowMetrics().getBounds();
//            screenWidth = rect.width();
//        } else {
//            DisplayMetrics dm = new DisplayMetrics();
//            // 从默认显示器中获取显示参数保存到dm对象中
//            wm.getDefaultDisplay().getMetrics(dm);
//            screenWidth = dm.widthPixels;
//        }
//        return screenWidth; // 返回屏幕的宽度数值
    }

    // 获得屏幕的高度
    public static int getScreenHeight(Context ctx) {
        return ctx.getResources().getDisplayMetrics().heightPixels;
//        int screenHeight;
//        // 从系统服务中获取窗口管理器
//        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            // 获取当前屏幕的四周边界
//            Rect rect = wm.getCurrentWindowMetrics().getBounds();
//            screenHeight = rect.height();
//        } else {
//            DisplayMetrics dm = new DisplayMetrics();
//            // 从默认显示器中获取显示参数保存到dm对象中
//            wm.getDefaultDisplay().getMetrics(dm);
//            screenHeight = dm.heightPixels;
//        }
//        return screenHeight; // 返回屏幕的高度数值
    }

}

