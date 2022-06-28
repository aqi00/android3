package com.example.chapter14.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateUtil {
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

    // 获取当前的时间（精确到毫秒）
    public static String getNowTimeDetail() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        return sdf.format(new Date());
    }

    // 把日历实例格式化为字符串
    public static String getDate(Calendar calendar) {
        Date date = calendar.getTime();
        // 创建一个日期格式化的工具
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 将当前日期时间按照指定格式输出格式化后的日期时间字符串
        return sdf.format(date);
    }

}
