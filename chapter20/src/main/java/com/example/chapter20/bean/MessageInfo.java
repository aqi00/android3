package com.example.chapter20.bean;

public class MessageInfo {
    public String from; // 消息的发送者
    public String to; // 消息的接收者
    public String content; // 消息内容

    public MessageInfo(String from, String to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
    }
}
