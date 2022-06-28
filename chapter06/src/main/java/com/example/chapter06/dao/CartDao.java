package com.example.chapter06.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.chapter06.entity.CartInfo;
import com.example.chapter06.util.DateUtil;

import java.util.List;

@Dao
public interface CartDao {

    @Query("SELECT * FROM CartInfo") // 设置查询语句
    List<CartInfo> queryAllCart(); // 加载所有购物车信息

    @Query("SELECT * FROM CartInfo WHERE goodsId = :goodsId") // 设置带条件的查询语句
    CartInfo queryCartByGoodsId(long goodsId); // 根据名字加载购物车

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 记录重复时替换原记录
    void insertOneCart(CartInfo cart); // 插入一条购物车信息

    @Insert
    void insertCartList(List<CartInfo> cartList); // 插入多条购物车信息

    @Update(onConflict = OnConflictStrategy.REPLACE)// 出现重复记录时替换原记录
    int updateCart(CartInfo cart); // 更新购物车信息

    @Delete
    void deleteCart(CartInfo cart); // 删除购物车信息

    @Query("DELETE FROM CartInfo WHERE goodsId = :goodsId") // 设置删除语句
    void deleteOneCart(long goodsId); // 删除一条购物车信息

    @Query("DELETE FROM CartInfo WHERE 1=1") // 设置删除语句
    void deleteAllCart(); // 删除所有购物车信息

    default void save(long goodsId) {
        CartInfo cartInfo = queryCartByGoodsId(goodsId);
        if (cartInfo == null) {
            cartInfo = new CartInfo();
            cartInfo.setGoodsId(goodsId);
            cartInfo.setCount(1);
            cartInfo.setUpdateTime(DateUtil.getNowDateTime(""));
            insertOneCart(cartInfo);
        } else {
            cartInfo.setCount(cartInfo.getCount()+1);
            cartInfo.setUpdateTime(DateUtil.getNowDateTime(""));
            updateCart(cartInfo);
        }
    }
}
