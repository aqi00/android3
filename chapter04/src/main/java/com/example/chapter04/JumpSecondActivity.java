package com.example.chapter04;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class JumpSecondActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump_second);
        findViewById(R.id.btn_jump_first).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_jump_first) {
            // 创建一个意图对象，准备跳到指定的活动页面
            Intent intent = new Intent(this, JumpFirstActivity.class);
            // 当栈中存在待跳转的活动实例时，则重新创建该活动的实例，并清除原实例上方的所有实例
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 设置启动标志
            startActivity(intent); // 跳转到意图指定的活动页面
        }
    }
}
