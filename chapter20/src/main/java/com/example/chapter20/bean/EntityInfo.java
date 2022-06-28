package com.example.chapter20.bean;

public class EntityInfo {
    public String name; // 实体名称
    public String relation; // 实体关系
    public Object info; // 实体信息

    public EntityInfo(String name, String relation) {
        this.name = name;
        this.relation = relation;
    }

    public EntityInfo(String name, String relation, Object info) {
        this.name = name;
        this.relation = relation;
        this.info = info;
    }

}
