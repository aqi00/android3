package com.example.chapter04.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

public class ShockReceiver extends BroadcastReceiver {
    private static final String TAG = "ShockReceiver";
    // 静态注册时候的action、发送广播时的action、接收广播时的action，三者需要保持一致
    public static final String SHOCK_ACTION = "com.example.chapter04.shock";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        if (intent.getAction().equals(ShockReceiver.SHOCK_ACTION)){
            // 从系统服务中获取震动管理器
            Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vb.vibrate(500); // 命令震动器吱吱个若干秒，这里的500表示500毫秒
        }
    }

}
