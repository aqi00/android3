package com.example.chapter06;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter06.bean.UserInfo;
import com.example.chapter06.database.UserDBHelper;
import com.example.chapter06.util.DateUtil;
import com.example.chapter06.util.ToastUtil;

public class SQLiteWriteActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private UserDBHelper mHelper; // 声明一个用户数据库帮助器的对象
    private EditText et_name; // 声明一个编辑框对象
    private EditText et_age; // 声明一个编辑框对象
    private EditText et_height; // 声明一个编辑框对象
    private EditText et_weight; // 声明一个编辑框对象
    private boolean isMarried = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite_write);
        et_name = findViewById(R.id.et_name);
        et_age = findViewById(R.id.et_age);
        et_height = findViewById(R.id.et_height);
        et_weight = findViewById(R.id.et_weight);
        CheckBox ck_married = findViewById(R.id.ck_married);
        ck_married.setOnCheckedChangeListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isMarried = isChecked;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 获得数据库帮助器的实例
        mHelper = UserDBHelper.getInstance(this, 1);
        mHelper.openWriteLink(); // 打开数据库帮助器的写连接
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHelper.closeLink(); // 关闭数据库连接
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save) {
            String name = et_name.getText().toString();
            String age = et_age.getText().toString();
            String height = et_height.getText().toString();
            String weight = et_weight.getText().toString();
            if (TextUtils.isEmpty(name)) {
                ToastUtil.show(this, "请先填写姓名");
                return;
            } else if (TextUtils.isEmpty(age)) {
                ToastUtil.show(this, "请先填写年龄");
                return;
            } else if (TextUtils.isEmpty(height)) {
                ToastUtil.show(this, "请先填写身高");
                return;
            } else if (TextUtils.isEmpty(weight)) {
                ToastUtil.show(this, "请先填写体重");
                return;
            }
            // 以下声明一个用户信息对象，并填写它的各字段值
            UserInfo info = new UserInfo();
            info.name = name;
            info.age = Integer.parseInt(age);
            info.height = Long.parseLong(height);
            info.weight = Float.parseFloat(weight);
            info.married = isMarried;
            info.update_time = DateUtil.getNowDateTime("yyyy-MM-dd HH:mm:ss");
            mHelper.insert(info); // 执行数据库帮助器的插入操作
            ToastUtil.show(this, "数据已写入SQLite数据库");
        }
    }

}
