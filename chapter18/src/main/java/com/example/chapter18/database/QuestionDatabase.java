package com.example.chapter18.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.chapter18.dao.QuestionDao;
import com.example.chapter18.entity.PoemInfo;
import com.example.chapter18.entity.QuestionInfo;

//entities表示该数据库有哪些表，version表示数据库的版本号
//exportSchema表示是否导出数据库信息的json串，建议设为false，若设为true还需指定json文件的保存路径
@Database(entities = {QuestionInfo.class, PoemInfo.class},version = 1, exportSchema = false)
public abstract class QuestionDatabase extends RoomDatabase {
    // 获取该数据库中某张表的持久化对象
    public abstract QuestionDao questionDao();
}
