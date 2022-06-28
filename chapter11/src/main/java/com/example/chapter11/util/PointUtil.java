package com.example.chapter11.util;

import android.graphics.PointF;

public class PointUtil {

    // 计算两个坐标点之间的距离
    public static float distance(PointF p1, PointF p2) {
        float offsetX = p2.x - p1.x;
        float offsetY = p2.y - p1.y;
        return (float) Math.sqrt(offsetX * offsetX + offsetY * offsetY);
    }

    // 计算两个坐标点构成的角度
    public static int degree(PointF p1, PointF p2) {
        return (int) (Math.atan((p2.y - p1.y) / (p2.x - p1.x)) / Math.PI * 180);
    }

}
