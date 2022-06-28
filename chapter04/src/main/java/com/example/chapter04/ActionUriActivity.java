package com.example.chapter04;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ActionUriActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_uri);
        findViewById(R.id.btn_dial).setOnClickListener(this);
        findViewById(R.id.btn_sms).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String phoneNo = "12345";
        if (v.getId() == R.id.btn_dial) { // 点击了“跳到拨号页面”按钮
            Intent intent = new Intent(); // 创建一个新意图
            intent.setAction(Intent.ACTION_DIAL); // 设置意图动作为准备拨号
            Uri uri = Uri.parse("tel:" + phoneNo); // 声明一个拨号的Uri
            intent.setData(uri); // 设置意图前往的路径
            startActivity(intent); // 启动意图通往的活动页面
        } else if (v.getId() == R.id.btn_sms) { // 点击了“跳到短信页面”按钮
            Intent intent = new Intent(); // 创建一个新意图
            intent.setAction(Intent.ACTION_SENDTO); // 设置意图动作为发短信
            Uri uri = Uri.parse("smsto:" + phoneNo); // 声明一个发送短信的Uri
            intent.setData(uri); // 设置意图前往的路径
            startActivity(intent); // 启动意图通往的活动页面
        }
    }

}
