package com.example.chapter06.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.chapter06.dao.BookDao;
import com.example.chapter06.entity.BookInfo;

//entities表示该数据库有哪些表，version表示数据库的版本号
//exportSchema表示是否导出数据库信息的json串，建议设为false，若设为true还需指定json文件的保存路径
@Database(entities = {BookInfo.class},version = 1, exportSchema = false)
public abstract class BookDatabase extends RoomDatabase {
    // 获取该数据库中某张表的持久化对象
    public abstract BookDao bookDao();
}
