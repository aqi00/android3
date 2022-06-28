package com.example.chapter13.bean;

public class ImageMessage {
    public String from; // 消息的发送者
    public String to; // 消息的接收者
    private ImagePart part; // 图片片段

    public ImageMessage() {
    }

    public ImageMessage(String from, String to, ImagePart part) {
        this.from = from;
        this.to = to;
        this.part = part;
    }
    
    public void setFrom(String from) {
    	this.from = from;
    }
    
    public String getFrom() {
    	return this.from;
    }

    public void setTo(String to) {
    	this.to = to;
    }
    
    public String getTo() {
    	return this.to;
    }

    public void setPart(ImagePart part) {
    	this.part = part;
    }
    
    public ImagePart getPart() {
    	return this.part;
    }
    
}
