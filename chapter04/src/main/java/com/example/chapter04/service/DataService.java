package com.example.chapter04.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class DataService extends Service {
    private static final String TAG = "DataService";
    private final IBinder mBinder = new LocalBinder(); // 创建一个粘合剂对象
    // 定义一个当前服务的粘合剂，用于将该服务黏到活动页面的进程中
    public class LocalBinder extends Binder {
        public DataService getService() {
            return DataService.this;
        }

        // 获取数字描述
        public String getNumber(int number) {
            return "我收到了数字"+number;
        }
    }

    @Override
    public IBinder onBind(Intent intent) { // 绑定服务。返回该服务的粘合剂对象
        Log.d(TAG, "绑定服务开始旅程！");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) { // 解绑服务
        Log.d(TAG, "绑定服务结束旅程！");
        return true; // 返回false表示只能绑定一次，返回true表示允许多次绑定
    }
}