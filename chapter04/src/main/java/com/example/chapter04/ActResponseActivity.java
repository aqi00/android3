package com.example.chapter04;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter04.util.DateUtil;

public class ActResponseActivity extends AppCompatActivity implements View.OnClickListener {
    private String mResponse = "我吃过了，还是你来我家吃";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_response);
        // 从布局文件中获取名叫tv_request的文本视图
        TextView tv_request = findViewById(R.id.tv_request);
        findViewById(R.id.btn_response).setOnClickListener(this);
        // 从布局文件中获取名叫tv_response的文本视图
        TextView tv_response = findViewById(R.id.tv_response);
        tv_response.setText("待返回的消息为："+mResponse);
        // 从上一个页面传来的意图中获取快递包裹
        Bundle bundle = getIntent().getExtras();
        // 从包裹中取出名叫request_time的字符串
        String request_time = bundle.getString("request_time");
        // 从包裹中取出名叫request_content的字符串
        String request_content = bundle.getString("request_content");
        String desc = String.format("收到请求消息：\n请求时间为：%s\n请求内容为：%s",
                request_time, request_content);
        tv_request.setText(desc); // 把请求消息的详情显示在文本视图上
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_response) {
            Intent intent = new Intent(); // 创建一个新意图
            Bundle bundle = new Bundle(); // 创建一个新包裹
            // 往包裹存入名叫response_time的字符串
            bundle.putString("response_time", DateUtil.getNowTime());
            // 往包裹存入名叫response_content的字符串
            bundle.putString("response_content", mResponse);
            intent.putExtras(bundle); // 把快递包裹塞给意图
            // 携带意图返回上一个页面。RESULT_OK表示处理成功
            setResult(Activity.RESULT_OK, intent);
            finish(); // 结束当前的活动页面
        }
    }
}
