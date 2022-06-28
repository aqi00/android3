package com.example.chapter06;

import android.util.Log;

import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import com.example.chapter06.database.BookDatabase;
import com.example.chapter06.database.CartDatabase;
import com.example.chapter06.database.GoodsDatabase;

import java.util.HashMap;

public class MainApplication extends MultiDexApplication {
    private final static String TAG = "MainApplication";
    private static MainApplication mApp; // 声明一个当前应用的静态实例
    // 声明一个公共的信息映射对象，可当作全局变量使用
    public HashMap<String, String> infoMap = new HashMap<String, String>();
    public static int goodsCount = 0;

    private BookDatabase bookDatabase; // 声明一个书籍数据库对象
    private CartDatabase cartDatabase; // 声明一个购物车数据库对象
    private GoodsDatabase goodsDatabase; // 声明一个商品数据库对象

    // 利用单例模式获取当前应用的唯一实例
    public static MainApplication getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mApp = this; // 在打开应用时对静态的应用实例赋值

        // 构建书籍数据库的实例
        bookDatabase = Room.databaseBuilder(mApp, BookDatabase.class,"BookInfo")
                .addMigrations() // 允许迁移数据库（发生数据库变更时，Room默认删除原数据库再创建新数据库。如此一来原来的记录会丢失，故而要改为迁移方式以便保存原有记录）
                .allowMainThreadQueries() // 允许在主线程中操作数据库（Room默认不能在主线程中操作数据库）
                .build();
        // 构建购物车数据库的实例
        cartDatabase = Room.databaseBuilder(mApp, CartDatabase.class,"CartInfo")
                .addMigrations().allowMainThreadQueries().build();
        // 构建商品数据库的实例
        goodsDatabase = Room.databaseBuilder(mApp, GoodsDatabase.class,"GoodsInfo")
                .addMigrations().allowMainThreadQueries().build();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "onTerminate");
    }

    // 获取书籍数据库的实例
    public BookDatabase getBookDB(){
        return bookDatabase;
    }

    // 获取购物车数据库的实例
    public CartDatabase getCartDB(){
        return cartDatabase;
    }

    // 获取商品数据库的实例
    public GoodsDatabase getGoodsDB(){
        return goodsDatabase;
    }
}
