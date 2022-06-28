package com.example.chapter06.bean;

//用户信息
public class UserInfo {
    public long rowid; // 行号
    public int xuhao; // 序号
    public String name; // 姓名
    public int age; // 年龄
    public long height; // 身高
    public float weight; // 体重
    public boolean married; // 婚否
    public String update_time; // 更新时间
    public String phone; // 手机号
    public String password; // 密码

    public UserInfo() {
        rowid = 0L;
        xuhao = 0;
        name = "";
        age = 0;
        height = 0L;
        weight = 0.0f;
        married = false;
        update_time = "";
        phone = "";
        password = "";
    }
}
