package com.example.chapter10.util;

import android.util.Log;

import com.example.chapter10.BuildConfig;

public class LogUtil {
    // 调试模式来自BuildConfig.DEBUG，false表示发布模式，true表示调试模式
    public static boolean isDebug = BuildConfig.DEBUG;

    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, msg); // 打印冗余日志
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg); // 打印调试日志
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg); // 打印一般日志
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, msg); // 打印警告日志
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg); // 打印错误日志
        }
    }
}
