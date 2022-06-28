package com.example.chapter11.bean;

import android.graphics.PointF;

// 定义一个路径位置实体类，包括上个落点的横纵坐标，以及下个落点的横纵坐标
public class PathPosition {
    public PointF prePos; // 上个落点的横纵坐标
    public PointF nextPos; // 下个落点的横纵坐标
}
