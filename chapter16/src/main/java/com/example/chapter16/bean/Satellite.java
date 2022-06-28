package com.example.chapter16.bean;

public class Satellite {
    public String name; // 卫星导航系统的名称
    public float signal; // 卫星的信噪比（信号）
    public float elevation; // 卫星的仰角
    public float azimuth; // 卫星的方位角
    public String time; // 当前时间

    public Satellite() {
        name = "";
        signal = -1;
        elevation = -1;
        azimuth = -1;
        time = "";
    }
}
