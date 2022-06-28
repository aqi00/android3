package com.example.chapter04;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginInputActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_input);
        findViewById(R.id.btn_jump_success).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_jump_success) {
            // 创建一个意图对象，准备跳到指定的活动页面
            Intent intent = new Intent(this, LoginSuccessActivity.class);
            // 设置启动标志：跳转到新页面时，栈中的原有实例都被清空，同时开辟新任务的活动栈
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent); // 跳转到意图指定的活动页面
        }
    }
}
