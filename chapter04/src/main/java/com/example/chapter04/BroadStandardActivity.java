package com.example.chapter04;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter04.util.DateUtil;

public class BroadStandardActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "BroadStandardActivity";
    // 这是广播的动作名称，发送广播和接收广播都以它作为接头暗号
    private final static String STANDARD_ACTION = "com.example.chapter04.standard";
    private TextView tv_standard; // 声明一个文本视图对象
    private String mDesc = "这里查看标准广播的收听信息";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broad_standard);
        tv_standard = findViewById(R.id.tv_standard);
        tv_standard.setText(mDesc);
        findViewById(R.id.btn_send_standard).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send_standard) {
            Intent intent = new Intent(STANDARD_ACTION); // 创建指定动作的意图
            sendBroadcast(intent); // 发送标准广播
        }
    }

    private StandardReceiver standardReceiver; // 声明一个标准广播的接收器实例
    @Override
    protected void onStart() {
        super.onStart();
        standardReceiver = new StandardReceiver(); // 创建一个标准广播的接收器
        // 创建一个意图过滤器，只处理STANDARD_ACTION的广播
        IntentFilter filter = new IntentFilter(STANDARD_ACTION);
        registerReceiver(standardReceiver, filter); // 注册接收器，注册之后才能正常接收广播
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(standardReceiver); // 注销接收器，注销之后就不再接收广播
    }

    // 定义一个标准广播的接收器
    private class StandardReceiver extends BroadcastReceiver {
        // 一旦接收到标准广播，马上触发接收器的onReceive方法
        @Override
        public void onReceive(Context context, Intent intent) {
            // 广播意图非空，且接头暗号正确
            if (intent != null && intent.getAction().equals(STANDARD_ACTION)) {
                mDesc = String.format("%s\n%s 收到一个标准广播", mDesc, DateUtil.getNowTime());
                tv_standard.setText(mDesc);
            }
        }
    }

}
