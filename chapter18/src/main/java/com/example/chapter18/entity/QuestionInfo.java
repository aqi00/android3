package com.example.chapter18.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class QuestionInfo {
    @PrimaryKey(autoGenerate = true) // 主键值自增
    @NonNull // 主键必须是非空字段
    private int id; // 编号
    private String question; // 问题
    private String answer; // 答案
    private int type; // 类型。0 必需的，不能删除；1 可选的，允许删除

    public QuestionInfo(String question, String answer, int type) {
        this.question = question;
        this.answer = answer;
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

}
