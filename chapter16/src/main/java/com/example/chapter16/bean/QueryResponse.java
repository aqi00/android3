package com.example.chapter16.bean;

import java.util.List;

public class QueryResponse {
    private String code; // 结果代码
    private String desc; // 结果描述
    private List<PersonInfo> personList; // 人员信息列表

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

    public void setPersonList(List<PersonInfo> personList) {
        this.personList = personList;
    }
    
    public List<PersonInfo> getPersonList() {
        return this.personList;
    }
}
