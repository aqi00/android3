package com.example.chapter14.bean;

import java.io.Serializable;

public class VideoInfo implements Serializable {
    private long id; // 编号
    private String title; // 标题
    private int duration; // 播放时长
    private long size; // 文件大小

    private String date; // 拍摄日期
    private String address; // 拍摄地址
    private String label; // 视频标签
    private String desc; // 视频描述
    private String cover; // 视频封面
    private String video; // 视频链接

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getDate() {
        return this.date;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddress() {
        return this.address;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public String getLabel() {
        return this.label;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getDesc() {
        return this.desc;
    }
    public void setCover(String cover) {
        this.cover = cover;
    }
    public String getCover() {
        return this.cover;
    }
    public void setVideo(String video) {
        this.video = video;
    }
    public String getVideo() {
        return this.video;
    }
}
