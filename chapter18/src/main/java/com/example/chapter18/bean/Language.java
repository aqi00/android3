package com.example.chapter18.bean;

import java.util.Locale;

public class Language {
    public String name; // 语言名称
    public String desc; // 描述
    public Locale locale; // 语言区域

    public Language(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public Language(String name, Locale locale) {
        this.name = name;
        this.locale = locale;
    }

}
