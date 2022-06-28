package com.example.chapter08;

import android.app.Application;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.example.chapter08.util.NotifyUtil;

public class MainApplication extends Application {
    private final static String TAG = "MainApplication";
    private static MainApplication mApp; // 声明一个当前应用的静态实例

    // 利用单例模式获取当前应用的唯一实例
    public static MainApplication getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 这里不能屏蔽通知渠道代码，因为后面活动会给该渠道发送通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0开始必须给每个通知分配对应的渠道
            NotifyUtil.createNotifyChannel(this, getString(R.string.app_name), getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW);
        }
        Log.d(TAG, "onCreate");
        mApp = this; // 在打开应用时对静态的应用实例赋值
    }

}
