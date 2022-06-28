package com.example.chapter06;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter06.bean.UserInfo;
import com.example.chapter06.provider.UserInfoContent;
import com.example.chapter06.util.ToastUtil;
import com.example.chapter06.util.Utils;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("DefaultLocale")
public class ContentReadActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ContentReadActivity";
    private TextView tv_desc; // 声明一个文本视图对象
    private LinearLayout ll_list; // 用户信息列表的线性布局

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_read);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        tv_desc = findViewById(R.id.tv_desc);
        ll_list = findViewById(R.id.ll_list);
        showAllUser(); // 显示所有的用户记录
    }

    // 显示所有的用户记录
    private void showAllUser() {
        List<UserInfo> userList = new ArrayList<UserInfo>();
        // 通过内容解析器从指定Uri中获取用户记录的游标
        Cursor cursor = getContentResolver().query(UserInfoContent.CONTENT_URI, null, null, null, null);
        // 循环取出游标指向的每条用户记录
        while (cursor.moveToNext()) {
            UserInfo user = new UserInfo();
            user.name = cursor.getString(cursor.getColumnIndex(UserInfoContent.USER_NAME));
            user.age = cursor.getInt(cursor.getColumnIndex(UserInfoContent.USER_AGE));
            user.height = cursor.getInt(cursor.getColumnIndex(UserInfoContent.USER_HEIGHT));
            user.weight = cursor.getFloat(cursor.getColumnIndex(UserInfoContent.USER_WEIGHT));
            userList.add(user); // 添加到用户信息列表
        }
        cursor.close(); // 关闭数据库游标
        String contactCount = String.format("当前共找到%d个用户", userList.size());
        tv_desc.setText(contactCount);
        ll_list.removeAllViews(); // 移除线性布局下面的所有下级视图
        for (UserInfo user : userList) { // 遍历用户信息列表
            String contactDesc = String.format("姓名为%s，年龄为%d，身高为%d，体重为%f\n",
                    user.name, user.age, user.height, user.weight);
            TextView tv_contact = new TextView(this); // 创建一个文本视图
            tv_contact.setText(contactDesc);
            tv_contact.setTextColor(Color.BLACK);
            tv_contact.setTextSize(17);
            int pad = Utils.dip2px(this, 5);
            tv_contact.setPadding(pad, pad, pad, pad); // 设置文本视图的内部间距
            ll_list.addView(tv_contact); // 把文本视图添加至线性布局
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_delete) {
            getContentResolver().delete(UserInfoContent.CONTENT_URI, "1=1", null);
            showAllUser();
            ToastUtil.show(this, "已删除所有记录");
        }
    }
}
