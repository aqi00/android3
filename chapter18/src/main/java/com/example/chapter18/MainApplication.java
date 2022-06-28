package com.example.chapter18;

import android.app.Application;
import android.util.Log;

import androidx.room.Room;

import com.example.chapter18.database.QuestionDatabase;

public class MainApplication extends Application {
    private final static String TAG = "MainApplication";
    private static MainApplication mApp; // 声明一个当前应用的静态实例
    private QuestionDatabase questionDatabase; // 声明一个问答数据库对象

    // 利用单例模式获取当前应用的唯一实例
    public static MainApplication getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mApp = this; // 在打开应用时对静态的应用实例赋值
        // 构建问答数据库的实例
        questionDatabase = Room.databaseBuilder(mApp, QuestionDatabase.class,"QuestionInfo")
                .addMigrations() // 允许迁移数据库（发生数据库变更时，Room默认删除原数据库再创建新数据库。如此一来原来的记录会丢失，故而要改为迁移方式以便保存原有记录）
                .allowMainThreadQueries() // 允许在主线程中操作数据库（Room默认不能在主线程中操作数据库）
                .build();
    }

    // 获取问答数据库的实例
    public QuestionDatabase getQuestionDB(){
        return questionDatabase;
    }

}
