package com.example.chapter15.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//书籍信息
@Entity
public class BookInfo {
    @PrimaryKey // 该字段是主键，不能重复
    @NonNull // 主键必须是非空字段
    private String fileName; // 书籍名称
    private String author; // 作者
    private String title; // 标题
    private int pageCount = 0; // 总页数
    public long size; // 文件大小
    private String path; // 路径

    public BookInfo(String fileName) {
        this.title = fileName.substring(fileName.lastIndexOf("/") + 1);
        this.fileName = fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageCount() {
        return this.pageCount;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return this.size;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

}
