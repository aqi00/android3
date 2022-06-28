package com.example.chapter15;

import android.app.Application;
import android.util.Log;

import androidx.room.Room;

import com.example.chapter15.database.BookDatabase;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;

import java.util.HashMap;

public class MainApplication extends Application {
    private final static String TAG = "MainApplication";
    private static MainApplication mApp; // 声明一个当前应用的静态实例
    private BookDatabase bookDatabase; // 声明一个书籍数据库对象

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
        qbSdkInit(); // 初始化TBS组件
    }

    // 获取书籍数据库的实例
    public BookDatabase getBookDB(){
        return bookDatabase;
    }

    private boolean isLoadTBS = false; // 是否成功加载
    // TBS的集成步骤如下：
    // 1、已经申请了外部存储的访问权限，并在运行时获得动态授权（因为授权之后才能下载x5内核，务必要先授权）
    // 2、在Application中初始化腾讯X5组件（首次运行还需下载x5内核）
    // 3、AndroidManifest.xml声明了名叫com.tencent.smtt.utils.FileProvider的provider
    // 初始化TBS组件
    public void qbSdkInit() {
        if (isLoadTBS) { // 如果已经成功加载过，就不必重复加载了
            return;
        }
        QbSdk.setDownloadWithoutWifi(true); //非WiFi情况下，主动下载TBS内核
        // 搜集本地TBS内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean isX5Core) {
                isLoadTBS = isX5Core;
                // x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d(TAG, " onViewInitFinished is " + isX5Core);
            }

            @Override
            public void onCoreInitFinished() {
                Log.d(TAG, " onCoreInitFinished");
            }
        };
        // TBS内核初始化
        QbSdk.initX5Environment(getApplicationContext(), cb);
        // 以下设置会将Dex文件转为Oat的过程加以优化
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map); // 初始化TBS设置
    }

}
