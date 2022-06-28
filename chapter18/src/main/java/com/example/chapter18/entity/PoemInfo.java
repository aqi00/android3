package com.example.chapter18.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.chapter18.util.PinyinUtil;

@Entity
public class PoemInfo {
    @PrimaryKey(autoGenerate = true) // 主键值自增
    @NonNull // 主键必须是非空字段
    private int id; // 编号
    private String title; // 诗歌标题
    private String author; // 诗歌作者
    private String dynasty; // 作者朝代
    private String content; // 诗歌内容
    private String pinyin; // 标题的拼音

    public PoemInfo(String title, String author, String dynasty, String content) {
        this.title = title;
        this.author = author;
        this.dynasty = dynasty;
        this.content = content;
        String[] splits = title.split("·");
        String shortTitle = splits.length>1 ? splits[1] : title;
        String hanzi = shortTitle.length()<=4 ? shortTitle : shortTitle.substring(0, 4);
        this.pinyin = PinyinUtil.getHanziPinYin(hanzi, false);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }

    public String getDynasty() {
        return this.dynasty;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getPinyin() {
        return this.pinyin;
    }

}
