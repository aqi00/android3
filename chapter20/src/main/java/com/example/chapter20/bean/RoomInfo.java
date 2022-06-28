package com.example.chapter20.bean;

import java.util.Map;

public class RoomInfo {
    private String anchor_name; // 主播名称
    private String room_name; // 房间名称
    private Map<String, String> member_map; // 成员映射

    public RoomInfo(String anchor_name, String room_name, Map<String, String> member_map) {
        this.anchor_name = anchor_name;
        this.room_name = room_name;
        this.member_map = member_map;
    }

    public void setAnchor_name(String anchor_name) {
        this.anchor_name = anchor_name;
    }

    public String getAnchor_name() {
        return this.anchor_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getRoom_name() {
        return this.room_name;
    }

    public void setmember_map(Map<String, String> member_map) {
        this.member_map = member_map;
    }

    public Map<String, String> getMember_map() {
        return this.member_map;
    }

}
