package com.example.chapter19.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class PersonInfo {
    @PrimaryKey // 该字段是主键，不能重复
    @NonNull // 主键必须是非空字段
    private String name; // 姓名
    private String info; // 信息
    private int flag; // 识别标志。0 待识别；1 已识别
    private String time; // 识别时间
    private String location; // 识别地点
    @Ignore // 该属性不属于表字段
    private List<PersonPortrait> portraitList; // 头像列表

    public PersonInfo(String name, String info) {
        this.location = name.substring(name.lastIndexOf("/") + 1);
        this.name = name;
        this.info = info;
        flag = 0;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return this.info;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return this.flag;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return this.time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return this.location;
    }

    public void setPortraitList(List<PersonPortrait> portraitList) {
        this.portraitList = portraitList;
    }

    public List<PersonPortrait> getPortraitList() {
        return this.portraitList;
    }

}
