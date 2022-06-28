package com.example.chapter11;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class KeySoftActivity extends AppCompatActivity implements OnKeyListener {
    private TextView tv_result; // 声明一个文本视图对象
    private String desc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_soft);
        EditText et_soft = findViewById(R.id.et_soft);
        et_soft.setOnKeyListener(this); // 设置编辑框的按键监听器
        tv_result = findViewById(R.id.tv_result);
    }

    // 在发生按键动作时触发
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            desc = String.format("%s软按键编码是%d，动作是按下", desc, keyCode);
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                desc = String.format("%s，按键为回车键", desc);
            } else if (keyCode == KeyEvent.KEYCODE_DEL) {
                desc = String.format("%s，按键为删除键", desc);
            } else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                desc = String.format("%s，按键为搜索键", desc);
            } else if (keyCode == KeyEvent.KEYCODE_BACK) {
                desc = String.format("%s，按键为返回键", desc);
                // 延迟3秒后启动页面关闭任务
                new Handler(Looper.myLooper()).postDelayed(() -> finish(), 3000);
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                desc = String.format("%s，按键为加大音量键", desc);
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                desc = String.format("%s，按键为减小音量键", desc);
            }
            desc = desc + "\n";
            tv_result.setText(desc);
            // 返回true表示处理完了不再输入该字符，返回false表示给你输入该字符吧
            return true;
        } else {
            // 返回true表示处理完了不再输入该字符，返回false表示给你输入该字符吧
            return false;
        }
    }

}
