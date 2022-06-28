package com.example.chapter06;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.chapter06.dao.BookDao;
import com.example.chapter06.entity.BookInfo;
import com.example.chapter06.util.ToastUtil;

import java.util.List;

public class RoomReadActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_room; // 声明一个文本视图对象
    private BookDao bookDao; // 声明一个书籍的持久化对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_read);
        tv_room = findViewById(R.id.tv_room);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        // 从App实例中获取唯一的书籍持久化对象
        bookDao = MainApplication.getInstance().getBookDB().bookDao();
        readRoom(); // 读取数据库中的所有书籍记录
    }

    // 读取数据库中的所有书籍记录
    private void readRoom() {
        List<BookInfo> bookList = bookDao.queryAllBook(); // 获取所有书籍记录
        String desc = String.format("数据库查询到%d条记录，详情如下：", bookList.size());
        for (int i = 0; i < bookList.size(); i++) {
            BookInfo info = bookList.get(i);
            desc = String.format("%s\n第%d条记录信息如下：", desc, i + 1);
            desc = String.format("%s\n　书名为《%s》", desc, info.getName());
            desc = String.format("%s\n　作者为%s", desc, info.getAuthor());
            desc = String.format("%s\n　出版社为%s", desc, info.getPress());
            desc = String.format("%s\n　价格为%f", desc, info.getPrice());
        }
        if (bookList.size() <= 0) {
            desc = "数据库查询到的记录为空";
        }
        tv_room.setText(desc);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_delete) {
            bookDao.deleteAllBook(); // 删除所有书籍记录
            ToastUtil.show(this, "已删除所有记录");
            readRoom(); // 读取数据库中的所有书籍记录
        }
    }

}