package com.example.chapter08;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter08.util.ViewUtil;

public class NotifyCounterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_title; // 声明一个编辑框对象
    private EditText et_message; // 声明一个编辑框对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_counter);
        et_title = findViewById(R.id.et_title);
        et_message = findViewById(R.id.et_message);
        findViewById(R.id.btn_send_counter).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send_counter) {
            ViewUtil.hideOneInputMethod(this, et_message); // 隐藏输入法软键盘
            if (TextUtils.isEmpty(et_title.getText())) {
                Toast.makeText(this, "请填写消息标题", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(et_message.getText())) {
                Toast.makeText(this, "请填写消息内容", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = et_title.getText().toString();
            String message = et_message.getText().toString();
            sendCounterNotify(title, message); // 发送计时的通知消息
        }
    }

    // 发送计时的通知消息
    private void sendCounterNotify(String title, String message) {
        // 发送消息之前要先创建通知渠道，创建代码见MainApplication.java
        // 创建一个跳转到活动页面的意图
        Intent cancelIntent = new Intent(this, MainActivity.class);
        // 创建一个用于页面跳转的延迟意图
        PendingIntent deleteIntent = PendingIntent.getActivity(this,
                R.string.app_name, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 创建一个通知消息的建造器
        Notification.Builder builder = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0开始必须给每个通知分配对应的渠道
            builder = new Notification.Builder(this, getString(R.string.app_name));
        }
        builder.setDeleteIntent(deleteIntent) // 设置内容的清除意图
                .setSmallIcon(R.mipmap.ic_launcher) // 设置应用名称左边的小图标
                // 设置通知栏右边的大图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_app))
                .setProgress(100, 60, false) // 设置进度条及其具体进度
                .setUsesChronometer(true) // 设置是否显示计时器
                .setContentTitle(title) // 设置通知栏里面的标题文本
                .setContentText(message); // 设置通知栏里面的内容文本
        Notification notify = builder.build(); // 根据通知建造器构建一个通知对象
        // 从系统服务中获取通知管理器
        NotificationManager notifyMgr = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        // 使用通知管理器推送通知，然后在手机的通知栏就会看到该消息
        notifyMgr.notify(R.string.app_name, notify);
    }

}
