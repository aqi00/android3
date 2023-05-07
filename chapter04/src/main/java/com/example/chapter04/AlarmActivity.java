package com.example.chapter04;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter04.util.DateUtil;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AlarmActivity";
    private CheckBox ck_repeate;
    public TextView tv_alarm;
    private int mDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ck_repeate = findViewById(R.id.ck_repeate);
        tv_alarm = findViewById(R.id.tv_alarm);
        findViewById(R.id.btn_alarm).setOnClickListener(this);
        initDelaySpinner(); // 初始化闹钟延迟的下拉框
    }

    // 初始化闹钟延迟的下拉框
    private void initDelaySpinner() {
        ArrayAdapter<String> delayAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, delayDescArray);
        Spinner sp_delay = findViewById(R.id.sp_delay);
        sp_delay.setPrompt("请选择闹钟延迟");
        sp_delay.setAdapter(delayAdapter);
        sp_delay.setOnItemSelectedListener(new DelaySelectedListener());
        sp_delay.setSelection(0);
    }

    private int[] delayArray = {5, 10, 15, 20, 25, 30};
    private String[] delayDescArray = {"5秒", "10秒", "15秒", "20秒", "25秒", "30秒"};
    class DelaySelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mDelay = delayArray[arg2];
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_alarm) {
            sendAlarm(); // 发送闹钟广播
            mDesc = DateUtil.getNowTime() + " 设置闹钟";
            tv_alarm.setText(mDesc);
        }
    }

    // 发送闹钟广播
    private void sendAlarm() {
        Intent intent = new Intent(ALARM_ACTION); // 创建一个广播事件的意图
        // 从Android12开始，必须添加 FLAG_IMMUTABLE 或者 FLAG_MUTABLE
        // 创建一个用于广播的延迟意图
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        // 从系统服务中获取闹钟管理器
        AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        long delayTime = System.currentTimeMillis() + mDelay*1000; // 给当前时间加上若干秒
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 允许在空闲时发送广播，Android6.0之后新增的方法
            alarmMgr.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, delayTime, pIntent);
        } else {
            // 设置一次性闹钟，延迟若干秒后，携带延迟意图发送闹钟广播（但Android6.0之后，set方法在暗屏时不保证发送广播，必须调用setAndAllowWhileIdle方法）
            alarmMgr.set(AlarmManager.RTC_WAKEUP, delayTime, pIntent);
        }
//        // 设置重复闹钟，每隔一定间隔就发送闹钟广播（但从Android4.4开始，setRepeating方法不保证按时发送广播）
//        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
//                mDelay*1000, pIntent);
    }

    // 声明一个闹钟广播事件的标识串
    private String ALARM_ACTION = "com.example.chapter04.alarm";
    private String mDesc = ""; // 闹钟时间到达的描述

    // 定义一个闹钟广播的接收器
    public class AlarmReceiver extends BroadcastReceiver {
        // 一旦接收到闹钟时间到达的广播，马上触发接收器的onReceive方法
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                mDesc = String.format("%s\n%s 闹钟时间到达", mDesc, DateUtil.getNowTime());
                tv_alarm.setText(mDesc);
                // 从系统服务中获取震动管理器
                Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(500); // 命令震动器吱吱个若干秒
                if (ck_repeate.isChecked()) { // 需要重复闹钟广播
                    sendAlarm(); // 发送闹钟广播
                }
            }
        }
    }

    private AlarmReceiver alarmReceiver; // 声明一个闹钟的广播接收器
    @Override
    public void onStart() {
        super.onStart();
        alarmReceiver = new AlarmReceiver(); // 创建一个闹钟的广播接收器
        // 创建一个意图过滤器，只处理指定事件来源的广播
        IntentFilter filter = new IntentFilter(ALARM_ACTION);
        registerReceiver(alarmReceiver, filter); // 注册接收器，注册之后才能正常接收广播
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(alarmReceiver); // 注销接收器，注销之后就不再接收广播
    }

}
