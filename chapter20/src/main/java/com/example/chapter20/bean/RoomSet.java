package com.example.chapter20.bean;

import java.util.List;

public class RoomSet {
    private List<RoomInfo> room_list; // 房间列表
    
    public RoomSet(List<RoomInfo> room_list) {
        this.room_list = room_list;
    }

    public void setRoom_list(List<RoomInfo> room_list) {
        this.room_list = room_list;
    }
    
    public List<RoomInfo> getRoom_list() {
        return this.room_list;
    }

}
