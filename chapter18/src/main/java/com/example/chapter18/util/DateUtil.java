package com.example.chapter18.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
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

    // 获取当前的分钟
    public static String getNowMinute() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }

    // 获取当前的时间（精确到毫秒）
    public static String getNowTimeDetail() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        return sdf.format(new Date());
    }

    // 将长整型的时间数值格式化为日期时间字符串
    public static String formatDate(long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static String getNowDateCN() {
        Date date = new Date();
        return String.format("%d月%d日", date.getMonth()+1, date.getDate());
    }

    public static String getNowTimeCN() {
        Date date = new Date();
        return String.format("%d点%d分", date.getHours(), date.getMinutes());
    }

    private static String[] WEEK_NUM = {"天", "一", "二", "三", "四", "五", "六"};
    public static String getNowWeekCN() {
        Date date = new Date();
        return "星期"+WEEK_NUM[date.getDay()];
    }

}
