package com.example.chapter06;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.chapter06.dao.BookDao;
import com.example.chapter06.entity.BookInfo;
import com.example.chapter06.util.ToastUtil;

public class RoomWriteActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_name; // 声明一个编辑框对象
    private EditText et_author; // 声明一个编辑框对象
    private EditText et_press; // 声明一个编辑框对象
    private EditText et_price; // 声明一个编辑框对象
    private BookDao bookDao; // 声明一个书籍的持久化对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_write);
        et_name = findViewById(R.id.et_name);
        et_author = findViewById(R.id.et_author);
        et_press = findViewById(R.id.et_press);
        et_price = findViewById(R.id.et_price);
        findViewById(R.id.btn_save).setOnClickListener(this);
        // 从App实例中获取唯一的书籍持久化对象
        bookDao = MainApplication.getInstance().getBookDB().bookDao();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save) {
            String name = et_name.getText().toString();
            String author = et_author.getText().toString();
            String press = et_press.getText().toString();
            String price = et_price.getText().toString();
            if (TextUtils.isEmpty(name)) {
                ToastUtil.show(this, "请先填写书籍名称");
                return;
            } else if (TextUtils.isEmpty(author)) {
                ToastUtil.show(this, "请先填写作者姓名");
                return;
            } else if (TextUtils.isEmpty(press)) {
                ToastUtil.show(this, "请先填写出版社名称");
                return;
            } else if (TextUtils.isEmpty(price)) {
                ToastUtil.show(this, "请先填写价格");
                return;
            }
            // 以下声明一个书籍信息对象，并填写它的各字段值
            BookInfo info = new BookInfo();
            info.setName(name);
            info.setAuthor(author);
            info.setPress(press);
            info.setPrice(Double.parseDouble(price));
            bookDao.insertOneBook(info); // 往数据库插入一条书籍记录
            ToastUtil.show(this, "数据已写入Room数据库");
        }
    }

}