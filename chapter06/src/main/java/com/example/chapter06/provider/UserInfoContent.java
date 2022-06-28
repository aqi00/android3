package com.example.chapter06.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.chapter06.database.UserDBHelper;

public class UserInfoContent implements BaseColumns {
    // 这里的名称必须与AndroidManifest.xml里的android:authorities保持一致
    public static final String AUTHORITIES = "com.example.chapter06.provider.UserInfoProvider";
    //  内容提供器的外部表名
    public static final String TABLE_NAME = UserDBHelper.TABLE_NAME;
    // 访问内容提供器的URI
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITIES + "/user");
    // 下面是该表的各个字段名称
    public static final String USER_NAME = "name";
    public static final String USER_AGE = "age";
    public static final String USER_HEIGHT = "height";
    public static final String USER_WEIGHT = "weight";
    public static final String USER_MARRIED = "married";
    // 默认的排序方法
    public static final String DEFAULT_SORT_ORDER = "_id desc";
}
