package com.example.chapter06.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//书籍信息
@Entity
public class BookInfo {
    @PrimaryKey // 该字段是主键，不能重复
    @NonNull // 主键必须是非空字段
    private String name; // 书籍名称
    private String author; // 作者
    private String press; // 出版社
    private double price; // 价格

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public String getPress() {
        return this.press;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return this.price;
    }

}
