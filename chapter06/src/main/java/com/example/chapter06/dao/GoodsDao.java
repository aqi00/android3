package com.example.chapter06.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.chapter06.entity.GoodsInfo;

import java.util.List;

@Dao
public interface GoodsDao {

    @Query("SELECT * FROM GoodsInfo") // 设置查询语句
    List<GoodsInfo> queryAllGoods(); // 加载所有商品信息

    @Query("SELECT * FROM GoodsInfo WHERE id = :id") // 设置带条件的查询语句
    GoodsInfo queryGoodsById(long id); // 根据名字加载商品

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 记录重复时替换原记录
    long insertOneGoods(GoodsInfo goods); // 插入一条商品信息

    @Insert
    void insertGoodsList(List<GoodsInfo> goodsList); // 插入多条商品信息

    @Update(onConflict = OnConflictStrategy.REPLACE)// 出现重复记录时替换原记录
    int updateGoods(GoodsInfo goods); // 更新商品信息

    @Delete
    void deleteGoods(GoodsInfo goods); // 删除商品信息

    @Query("DELETE FROM GoodsInfo WHERE 1=1") // 设置删除语句
    void deleteAllGoods(); // 删除所有商品信息
}
