package com.example.chapter04;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter04.service.BindImmediateService;
import com.example.chapter04.util.DateUtil;

public class BindImmediateActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "BindImmediateActivity";
    private static TextView tv_immediate; // 声明一个文本视图对象
    private Intent mIntent; // 声明一个意图对象
    private static String mDesc; // 日志描述

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_immediate);
        tv_immediate = findViewById(R.id.tv_immediate);
        findViewById(R.id.btn_start_bind).setOnClickListener(this);
        findViewById(R.id.btn_unbind).setOnClickListener(this);
        mDesc = "";
        // 创建一个通往立即绑定服务的意图
        mIntent = new Intent(this, BindImmediateService.class);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start_bind) { // 点击了绑定服务按钮
            // 绑定服务。如果服务未启动，则系统先启动该服务再进行绑定
            boolean bindFlag = bindService(mIntent, mServiceConn, Context.BIND_AUTO_CREATE);
            Log.d(TAG, "bindFlag=" + bindFlag);
        } else if (v.getId() == R.id.btn_unbind) { // 点击了解绑服务按钮
            if (mBindService != null) {
                // 解绑服务。如果先前服务立即绑定，则此时解绑之后自动停止服务
                unbindService(mServiceConn);
            }
        }
    }

    public static void showText(String desc) {
        if (tv_immediate != null) {
            mDesc = String.format("%s%s %s\n", mDesc, DateUtil.getNowDateTime("HH:mm:ss"), desc);
            tv_immediate.setText(mDesc);
        }
    }

    private BindImmediateService mBindService; // 声明一个服务对象
    private ServiceConnection mServiceConn = new ServiceConnection() {
        // 获取服务对象时的操作
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 如果服务运行于另外一个进程，则不能直接强制转换类型，否则会报错
            mBindService = ((BindImmediateService.LocalBinder) service).getService();
            Log.d(TAG, "onServiceConnected");
        }

        // 无法获取到服务对象时的操作
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBindService = null;
            Log.d(TAG, "onServiceDisconnected");
        }
    };
}
