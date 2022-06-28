package com.example.chapter08;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter08.util.NotifyUtil;
import com.example.chapter08.util.ViewUtil;

public class NotifyChannelActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_title; // 声明一个编辑框对象
    private EditText et_message; // 声明一个编辑框对象
    private String mChannelId = "0"; // 通知渠道的编号
    private String mChannelName; // 通知渠道的名称
    private int mImportance; // 通知渠道的级别

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_channel);
        et_title = findViewById(R.id.et_title);
        et_message = findViewById(R.id.et_message);
        findViewById(R.id.btn_send_channel).setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initImportanceSpinner(); // 初始化渠道级别的下拉框
        }
    }

    // 初始化渠道级别的下拉框
    private void initImportanceSpinner() {
        findViewById(R.id.ll_channel).setVisibility(View.VISIBLE);
        ArrayAdapter<String> importanceAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, importanceDescArray);
        Spinner sp_importance = findViewById(R.id.sp_importance);
        sp_importance.setPrompt("请选择渠道级别");
        sp_importance.setAdapter(importanceAdapter);
        sp_importance.setSelection(3);
        sp_importance.setOnItemSelectedListener(new TypeSelectedListener());
    }

    private int[] importanceTypeArray = {NotificationManager.IMPORTANCE_NONE,
            NotificationManager.IMPORTANCE_MIN,
            NotificationManager.IMPORTANCE_LOW,
            NotificationManager.IMPORTANCE_DEFAULT,
            NotificationManager.IMPORTANCE_HIGH,
            NotificationManager.IMPORTANCE_MAX};
    private String[] importanceDescArray = {"不重要", // 无通知
            "最小级别", // 通知栏折叠，无提示声音，无锁屏通知
            "有点重要", // 通知栏展开，无提示声音，有锁屏通知
            "一般重要", // 通知栏展开，有提示声音，有锁屏通知
            "非常重要", // 通知栏展开，有提示声音，有锁屏通知，在屏幕顶部短暂悬浮（有的手机需要在设置页面开启横幅）
            "最高级别" // 通知栏展开，有提示声音，有锁屏通知，在屏幕顶部短暂悬浮（有的手机需要在设置页面开启横幅）
    };
    class TypeSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mImportance = importanceTypeArray[arg2];
            mChannelId = "" + arg2;
            mChannelName = importanceDescArray[arg2];
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    // 发送指定渠道的通知消息（包括消息标题和消息内容）
    private void sendChannelNotify(String title, String message) {
        // 创建一个跳转到活动页面的意图
        Intent clickIntent = new Intent(this, MainActivity.class);
        // 创建一个用于页面跳转的延迟意图
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                R.string.app_name, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 创建一个通知消息的建造器
        Notification.Builder builder = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0开始必须给每个通知分配对应的渠道
            builder = new Notification.Builder(this, mChannelId);
        }
        builder.setContentIntent(contentIntent) // 设置内容的点击意图
                .setAutoCancel(true) // 点击通知栏后是否自动清除该通知
                .setSmallIcon(R.mipmap.ic_launcher) // 设置应用名称左边的小图标
                .setContentTitle(title) // 设置通知栏里面的标题文本
                .setContentText(message); // 设置通知栏里面的内容文本
        Notification notify = builder.build(); // 根据通知建造器构建一个通知对象
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotifyUtil.createNotifyChannel(this, mChannelId, mChannelName, mImportance);
        }
        // 从系统服务中获取通知管理器
        NotificationManager notifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 使用通知管理器推送通知，然后在手机的通知栏就会看到该消息，多条通知需要指定不同的通知编号
        notifyMgr.notify(Integer.parseInt(mChannelId), notify);
        if (mImportance != NotificationManager.IMPORTANCE_NONE) {
            Toast.makeText(this, "已发送渠道消息", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send_channel) {
            ViewUtil.hideOneInputMethod(this, et_message); // 隐藏输入法软键盘
            if (TextUtils.isEmpty(et_title.getText())) {
                Toast.makeText(this, "请填写消息标题", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(et_message.getText())) {
                Toast.makeText(this, "请填写消息内容", Toast.LENGTH_SHORT).show();
                return;
            }
            // 发送指定渠道的通知消息（包括消息标题和消息内容）
            sendChannelNotify(et_title.getText().toString(), et_message.getText().toString());
        }
    }
}
