package com.example.chapter06.bean;

public class SmsContent {
    public String address; // 对方号码
    public String person; // 发送者的编号
    public String body; // 短信内容
    public String date; // 发送日期
    public int type; // 类型

    public SmsContent() {
        address = "";
        person = "";
        body = "";
        date = "";
        type = 0;
    }
}
