package com.example.chapter04;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

// 活动类直接实现点击监听器的接口View.OnClickListener
public class ActStartActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_start);
        // setOnClickListener来自View，故而允许直接给View对象注册点击监听器
        findViewById(R.id.btn_act_next).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) { // 点击事件的处理方法
        if (v.getId() == R.id.btn_act_next) {
            // 从当前页面跳到指定的新页面
            //startActivity(new Intent(ActStartActivity.this, ActFinishActivity.class));
            startActivity(new Intent(this, ActFinishActivity.class));
        }
    }
}
