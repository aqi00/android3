package com.example.chapter04;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.chapter04.util.DateUtil;

public class ActSendActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_send; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_send);
        // 从布局文件中获取名叫tv_send的文本视图
        tv_send = findViewById(R.id.tv_send);
        findViewById(R.id.btn_send).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send) {
            // 创建一个意图对象，准备跳到指定的活动页面
            Intent intent = new Intent(this, ActReceiveActivity.class);
            Bundle bundle = new Bundle(); // 创建一个新包裹
            // 往包裹存入名叫request_time的字符串
            bundle.putString("request_time", DateUtil.getNowTime());
            // 往包裹存入名叫request_content的字符串
            bundle.putString("request_content", tv_send.getText().toString());
            intent.putExtras(bundle); // 把快递包裹塞给意图
            startActivity(intent); // 跳转到意图指定的活动页面
        }
    }
}
