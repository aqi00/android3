package com.example.chapter04;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

// 活动类直接实现点击监听器的接口View.OnClickListener
public class ActFinishActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_finish);
        // 给箭头图标注册点击监听器，ImageView由View类派生而来
        findViewById(R.id.iv_back).setOnClickListener(this);
        // 给完成按钮注册点击监听器，Button也由View类派生而来
        findViewById(R.id.btn_finish).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) { // 点击事件的处理方法
        if (v.getId() == R.id.iv_back || v.getId() == R.id.btn_finish) {
            finish(); // 结束当前的活动页面
        }
    }
}
