package com.example.chapter04;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class JumpFirstActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump_first);
        findViewById(R.id.btn_jump_second).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_jump_second) {
            // 创建一个意图对象，准备跳到指定的活动页面
            Intent intent = new Intent(this, JumpSecondActivity.class);
            // 栈中存在待跳转的活动实例时，则重新创建该活动的实例，并清除原实例上方的所有实例
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 设置启动标志
            startActivity(intent); // 跳转到意图对象指定的活动页面
        }
    }
}
