package com.example.chapter10;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter10.util.LogUtil;

public class LogDebugActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "LogDebugActivity";
    private TextView tv_debug; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_debug);
        tv_debug = findViewById(R.id.tv_debug);
        findViewById(R.id.btn_debug).setOnClickListener(this);
        // 应用名称取自app_name，应用包名、版本号、版本名称均来自BuildConfig
        String desc = String.format("App调试标志为：%b", BuildConfig.DEBUG);
        tv_debug.setText(desc);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_debug) {
            Toast.makeText(this, "已点击按钮，请注意观察日志", Toast.LENGTH_SHORT).show();
            LogUtil.d(TAG, "您点击了测试按钮，只有在调试模式之下才能看到本日志");
            Log.d(TAG, "这条日志无论是否调试模式都能看到");
        }
    }

}
