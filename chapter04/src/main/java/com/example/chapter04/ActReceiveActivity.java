package com.example.chapter04;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ActReceiveActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_receive);
        // 从布局文件中获取名叫tv_receive的文本视图
        TextView tv_receive = findViewById(R.id.tv_receive);
        findViewById(R.id.btn_receive).setOnClickListener(this);
        // 从上一个页面传来的意图中获取快递包裹
        Bundle bundle = getIntent().getExtras();
        // 从包裹中取出名叫request_time的字符串
        String request_time = bundle.getString("request_time");
        // 从包裹中取出名叫request_content的字符串
        String request_content = bundle.getString("request_content");
        String desc = String.format("收到请求消息：\n请求时间为%s\n请求内容为%s",
                request_time, request_content);
        tv_receive.setText(desc); // 把请求消息的详情显示在文本视图上
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_receive) {
            finish(); // 结束当前的活动页面
        }
    }
}
