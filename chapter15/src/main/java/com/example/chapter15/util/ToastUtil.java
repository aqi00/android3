package com.example.chapter15.util;

import android.content.Context;
import android.widget.Toast;

import com.example.chapter15.BuildConfig;

public class ToastUtil {
    // 调试模式来自BuildConfig.DEBUG，false表示发布模式，true表示调试模式
    public static boolean isDebug = BuildConfig.DEBUG;

    // 不管发布模式还是调试模式，都弹出提示文字
    public static void show(Context ctx, String desc) {
        Toast.makeText(ctx, desc, Toast.LENGTH_SHORT).show();
    }

    // 调试模式下弹出短暂提示
    public static void showShort(Context ctx, String desc) {
        if (isDebug) {
            Toast.makeText(ctx, desc, Toast.LENGTH_SHORT).show();
        }
    }

    // 调试模式下弹出长久提示
    public static void showLong(Context ctx, String desc) {
        if (isDebug) {
            Toast.makeText(ctx, desc, Toast.LENGTH_LONG).show();
        }
    }
}
