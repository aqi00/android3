package com.example.chapter15.bean;

import java.util.ArrayList;
import java.util.List;

public class ParseResponse {
    private String code="0";
    private String desc;
    private String htmlPath;
    private List<String> pathList = new ArrayList<>();

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setHtmlPath(String htmlPath) {
        this.htmlPath = htmlPath;
    }

    public String getHtmlPath() {
        return this.htmlPath;
    }

    public void setPathList(List<String> pathList) {
        this.pathList = pathList;
    }

    public List<String> getPathList() {
        return this.pathList;
    }
}
