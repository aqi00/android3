package com.example.chapter18.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.chapter18.entity.PoemInfo;
import com.example.chapter18.entity.QuestionInfo;

import java.util.List;

@Dao
public interface QuestionDao {
    @Query("SELECT * FROM QuestionInfo") // 设置查询语句
    List<QuestionInfo> queryAllQuestion(); // 加载所有问答信息

    @Query("SELECT * FROM QuestionInfo where type=1") // 设置查询语句
    List<QuestionInfo> queryAllCustomQuestion(); // 加载所有问答信息

    @Query("SELECT * FROM QuestionInfo WHERE id = :id") // 设置带条件的查询语句
    QuestionInfo queryQuestionById(int id); // 根据编号加载问答信息

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 记录重复时替换原记录
    void insertOneQuestion(QuestionInfo question); // 插入一条问答信息

    @Insert
    void insertQuestionList(List<QuestionInfo> questionList); // 插入多条问答信息

    @Update(onConflict = OnConflictStrategy.REPLACE)// 出现重复记录时替换原记录
    int updateQuestion(QuestionInfo question); // 更新问答信息

    @Delete
    void deleteQuestion(QuestionInfo question); // 删除问答信息

    @Query("DELETE FROM QuestionInfo WHERE 1=1") // 设置删除语句
    void deleteAllQuestion(); // 删除所有问答信息

    @Query("SELECT * FROM PoemInfo") // 设置查询语句
    List<PoemInfo> queryAllPoem(); // 加载所有诗歌信息

    @Query("SELECT * FROM PoemInfo WHERE id = :id") // 设置带条件的查询语句
    PoemInfo queryPoemById(int id); // 根据编号加载诗歌信息

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 记录重复时替换原记录
    void insertOnePoem(PoemInfo question); // 插入一条诗歌信息

    @Insert
    void insertPoemList(List<PoemInfo> questionList); // 插入多条诗歌信息

    @Update(onConflict = OnConflictStrategy.REPLACE)// 出现重复记录时替换原记录
    int updatePoem(PoemInfo question); // 更新诗歌信息

    @Delete
    void deletePoem(PoemInfo question); // 删除诗歌信息

    @Query("DELETE FROM PoemInfo WHERE 1=1") // 设置删除语句
    void deleteAllPoem(); // 删除所有诗歌信息

}
