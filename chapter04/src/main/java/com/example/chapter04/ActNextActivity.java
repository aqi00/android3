package com.example.chapter04;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.chapter04.util.DateUtil;

public class ActNextActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "ActNextActivity";
    private TextView tv_life; // 声明一个文本视图对象
    private String mStr = "";

    private void refreshLife(String desc) { // 刷新生命周期的日志信息
        Log.d(TAG, desc);
        mStr = String.format("%s%s %s %s\n", mStr, DateUtil.getNowTimeDetail(), TAG, desc);
        tv_life.setText(mStr);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) { // 创建活动
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_next);
        findViewById(R.id.btn_act_pre).setOnClickListener(this);
        tv_life = findViewById(R.id.tv_life); // 从布局文件中获取名叫tv_life的文本视图
        refreshLife("onCreate");
    }

    @Override
    protected void onStart() { // 开始活动
        super.onStart();
        refreshLife("onStart"); // 刷新生命周期的日志信息
    }

    @Override
    protected void onStop() { // 停止活动
        super.onStop();
        refreshLife("onStop"); // 刷新生命周期的日志信息
    }

    @Override
    protected void onResume() { // 恢复活动
        super.onResume();
        refreshLife("onResume"); // 刷新生命周期的日志信息
    }

    @Override
    protected void onPause() { // 暂停活动
        super.onPause();
        refreshLife("onPause"); // 刷新生命周期的日志信息
    }

    @Override
    protected void onRestart() { // 重启活动
        super.onRestart();
        refreshLife("onRestart"); // 刷新生命周期的日志信息
    }

    @Override
    protected void onDestroy() { // 销毁活动
        super.onDestroy();
        refreshLife("onDestroy"); // 刷新生命周期的日志信息
    }

    @Override
    protected void onNewIntent(Intent intent) { // 重用已有的活动实例
        super.onNewIntent(intent);
        refreshLife("onNewIntent"); // 刷新生命周期的日志信息
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_act_pre) {
            finish(); // 结束当前的活动页面
        }
    }

}
