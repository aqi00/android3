package com.example.chapter19.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index("name")}) // 指定name为索引字段
public class PersonPortrait {
    @PrimaryKey(autoGenerate = true) // 主键值自增
    @NonNull // 主键必须是非空字段
    private int id; // 编号
    private String name; // 姓名
    private int type; // 头像类型。0 样本头像；1 识别头像
    private String path; // 头像文件的保存路径
    private double similarity; // 相似程度。值越大越相似，为1表示完全相同

    public PersonPortrait(String name, String path, int type) {
        this.name = name;
        this.path = path;
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public double getSimilarity() {
        return this.similarity;
    }

}
