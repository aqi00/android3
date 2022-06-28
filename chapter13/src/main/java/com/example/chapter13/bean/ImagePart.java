package com.example.chapter13.bean;

public class ImagePart {
    private String name; // 分段名称
    private String data; // 分段数据
    private int seq; // 分段序号
    private int length; // 分段长度

    public ImagePart(String name, String data, int seq, int length) {
        this.name = name;
        this.data = data;
        this.seq = seq;
        this.length = length;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return this.data;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return this.length;
    }

}
