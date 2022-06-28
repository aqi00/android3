package com.example.chapter19.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.chapter19.entity.PersonInfo;
import com.example.chapter19.entity.PersonPortrait;

import java.util.List;

@Dao
public interface PersonDao {

    @Query("SELECT * FROM PersonInfo") // 设置查询语句
    List<PersonInfo> queryAllPerson(); // 加载所有人员信息

    @Query("SELECT * FROM PersonInfo WHERE name = :name") // 设置带条件的查询语句
    PersonInfo queryPersonByName(String name); // 根据名字加载人员信息

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 记录重复时替换原记录
    void insertOnePerson(PersonInfo person); // 插入一条人员信息

    @Insert
    void insertPersonList(List<PersonInfo> personList); // 插入多条人员信息

    @Update(onConflict = OnConflictStrategy.REPLACE)// 出现重复记录时替换原记录
    int updatePerson(PersonInfo person); // 更新人员信息

    @Delete
    void deletePerson(PersonInfo person); // 删除人员信息

    @Query("DELETE FROM PersonInfo WHERE 1=1") // 设置删除语句
    void deleteAllPerson(); // 删除所有人员信息

    @Query("SELECT * FROM PersonPortrait WHERE name = :name and type = :type") // 设置查询语句
    List<PersonPortrait> queryPersonPortrait(String name, int type); // 根据名字加载人员头像

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 记录重复时替换原记录
    void insertOnePortrait(PersonPortrait portrait); // 插入一条人员头像

    @Insert
    void insertPortraitList(List<PersonPortrait> portraitList); // 插入多条人员头像

    @Delete
    void deletePortrait(PersonPortrait portrait); // 删除人员头像

    @Query("DELETE FROM PersonPortrait WHERE name = :name and (-1=:type or type=:type)") // 设置删除语句
    void deletePortraitByName(String name, int type); // 删除指定名称的人员头像

    @Query("DELETE FROM PersonPortrait WHERE 1=1") // 设置删除语句
    void deleteAllPortrait(); // 删除所有人员头像

}
