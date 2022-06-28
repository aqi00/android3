package com.example.chapter09.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateUtil {
    // 获取指定格式的日期时间
    public static String getNowDateTime(String formatStr) {
        String format = formatStr;
        if (TextUtils.isEmpty(format)) {
            format = "yyyyMMddHHmmss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    // 获取当前的日期时间
    public static String getNowDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    // 获取当前的时间
    public static String getNowTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

}
