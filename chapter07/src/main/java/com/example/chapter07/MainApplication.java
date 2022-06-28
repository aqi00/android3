package com.example.chapter07;

import android.app.Application;
import android.util.Log;

import com.example.chapter07.database.CartDBHelper;

public class MainApplication extends Application {
    private final static String TAG = "MainApplication";
    public static int goodsCount = 0;
    // 声明一个当前应用的静态实例
    private static MainApplication mApp;

    // 利用单例模式获取当前应用的唯一实例
    public static MainApplication getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mApp = this; // 在打开应用时对静态的应用实例赋值
        // 获取购物车数据库的帮助器对象
        CartDBHelper cartHelper = CartDBHelper.getInstance(this, 1);
        cartHelper.openReadLink(); // 打开购物车数据库的读连接
        goodsCount = cartHelper.queryCount(); // 查询购物车里面的商品数量
        cartHelper.closeLink(); // 关闭购物车数据库的读连接
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "onTerminate");
        super.onTerminate();
    }


}
