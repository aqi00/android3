package com.example.chapter05;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("DefaultLocale")
// 该页面实现了接口OnCheckedChangeListener，意味着要重写勾选监听器的onCheckedChanged方法
public class CheckBoxActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_box);
        // 从布局文件中获取名叫ck_system的复选框
        CheckBox ck_system = findViewById(R.id.ck_system);
        // 从布局文件中获取名叫ck_custom的复选框
        CheckBox ck_custom = findViewById(R.id.ck_custom);
        // 给ck_system设置勾选监听器，一旦用户点击复选框，就触发监听器的onCheckedChanged方法
        ck_system.setOnCheckedChangeListener(this);
        // 给ck_custom设置勾选监听器，一旦用户点击复选框，就触发监听器的onCheckedChanged方法
        ck_custom.setOnCheckedChangeListener(this);
        // 给ck_system设置勾选监听器，一旦用户点击复选框，就触发监听器的onCheckedChanged方法
        //ck_system.setOnCheckedChangeListener(new CheckListener());
        // 给ck_custom设置勾选监听器，一旦用户点击复选框，就触发监听器的onCheckedChanged方法
        //ck_custom.setOnCheckedChangeListener(new CheckListener());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String desc = String.format("您%s了这个CheckBox", isChecked ? "勾选" : "取消勾选");
        buttonView.setText(desc);
    }

    // 定义一个勾选监听器，它实现了接口CompoundButton.OnCheckedChangeListener
    private class CheckListener implements CompoundButton.OnCheckedChangeListener{
        // 在用户点击复选框时触发
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String desc = String.format("您勾选了控件%d，状态为%b", buttonView.getId(), isChecked);
            Toast.makeText(CheckBoxActivity.this, desc, Toast.LENGTH_LONG).show();
        }
    }
}
