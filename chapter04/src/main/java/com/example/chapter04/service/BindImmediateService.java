package com.example.chapter04.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.chapter04.BindImmediateActivity;

public class BindImmediateService extends Service {
    private static final String TAG = "BindImmediateService";
    private final IBinder mBinder = new LocalBinder(); // 创建一个粘合剂对象
    // 定义一个当前服务的粘合剂，用于将该服务黏到活动页面的进程中
    public class LocalBinder extends Binder {
        public BindImmediateService getService() {
            return BindImmediateService.this;
        }
    }

    private void refresh(String text) {
        Log.d(TAG, text);
        BindImmediateActivity.showText(text);
    }

    @Override
    public void onCreate() { // 创建服务
        super.onCreate();
        refresh("onCreate");
    }

    @Override
    public void onDestroy() { // 销毁服务
        super.onDestroy();
        refresh("onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) { // 绑定服务。返回该服务的粘合剂对象
        Log.d(TAG, "绑定服务开始旅程！");
        refresh("onBind");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) { // 重新绑定服务
        super.onRebind(intent);
        refresh("onRebind");
    }

    @Override
    public boolean onUnbind(Intent intent) { // 解绑服务
        Log.d(TAG, "绑定服务结束旅程！");
        refresh("onUnbind");
        return true; // 返回false表示只能绑定一次，返回true表示允许多次绑定
    }

}
