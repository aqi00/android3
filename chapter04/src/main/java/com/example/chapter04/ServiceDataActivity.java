package com.example.chapter04;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.chapter04.service.DataService;
import com.example.chapter04.util.DateUtil;

import java.util.Random;

@SuppressLint("SetTextI18n")
public class ServiceDataActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ServiceDataActivity";
    private TextView tv_result; // 声明一个文本视图对象
    private Intent mIntent; // 声明一个意图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_data);
        tv_result = findViewById(R.id.tv_result);
        findViewById(R.id.btn_start_bind).setOnClickListener(this);
        findViewById(R.id.btn_unbind).setOnClickListener(this);
        // 创建一个通往立即绑定服务的意图
        mIntent = new Intent(this, DataService.class);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start_bind) { // 点击了绑定服务按钮
            // 绑定服务。如果服务未启动，则系统先启动该服务再进行绑定
            boolean bindFlag = bindService(mIntent, mServiceConn, Context.BIND_AUTO_CREATE);
            Log.d(TAG, "bindFlag=" + bindFlag);
        } else if (v.getId() == R.id.btn_unbind) { // 点击了解绑服务按钮
            if (mBinder != null) {
                // 解绑服务。如果先前服务立即绑定，则此时解绑之后自动停止服务
                unbindService(mServiceConn);
                tv_result.setText(DateUtil.getNowTime()+" 成功解绑服务");
            }
        }
    }

    private DataService.LocalBinder mBinder; // 声明一个粘合剂对象
    private ServiceConnection mServiceConn = new ServiceConnection() {
        // 获取服务对象时的操作
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 如果服务运行于另外一个进程，则不能直接强制转换类型，否则会报错
            mBinder = (DataService.LocalBinder) service;
            // 活动代码通过粘合剂与服务代码通信
            String response = mBinder.getNumber(new Random().nextInt(100));
            tv_result.setText(DateUtil.getNowTime()+" 绑定服务应答："+response);
            Log.d(TAG, "onServiceConnected");
        }

        // 无法获取到服务对象时的操作
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder = null;
            Log.d(TAG, "onServiceDisconnected");
        }
    };
}