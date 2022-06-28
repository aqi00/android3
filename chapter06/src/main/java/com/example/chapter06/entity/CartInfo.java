package com.example.chapter06.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//购物车信息
@Entity
public class CartInfo {
    @PrimaryKey(autoGenerate = true) // 该字段是自增主键
    private long id; // 序号
    private long goodsId; // 商品编号
    private int count; // 商品数量
    private String updateTime; // 更新时间

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    public long getGoodsId() {
        return this.goodsId;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return this.count;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

}
