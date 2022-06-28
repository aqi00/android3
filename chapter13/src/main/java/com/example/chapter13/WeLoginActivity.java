package com.example.chapter13;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chapter13.constant.NetConst;
import com.example.chapter13.util.SocketUtil;

public class WeLoginActivity extends AppCompatActivity {
    private EditText et_name; // 声明一个编辑框对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_login);
        et_name = findViewById(R.id.et_name);
        findViewById(R.id.btn_login).setOnClickListener(v -> doLogin());
        // 检查能否连上Socket服务器
        SocketUtil.checkSocketAvailable(this, NetConst.CHAT_IP, NetConst.CHAT_PORT);
    }

    // 执行登录动作
    private void doLogin() {
        String name = et_name.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请输入您的微信昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        MainApplication.getInstance().wechatName = name;
        // 打开聊天界面
        startActivity(new Intent(this, WeChatActivity.class));
    }
}