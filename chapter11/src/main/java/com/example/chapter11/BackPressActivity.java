package com.example.chapter11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Toast;

public class BackPressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_press);
        Toolbar tl_head = findViewById(R.id.tl_head);
        tl_head.setTitle("返回按键");
        setSupportActionBar(tl_head); // 替换系统自带的ActionBar
        // 设置工具栏左侧导航图标的点击监听器
        tl_head.setNavigationOnClickListener(view -> finish());
    }

    private boolean needExit = false; // 是否退出当前页面

    // 在按下返回键时触发
    @Override
    public void onBackPressed() {
        if (needExit) {
            finish(); // 关闭当前页面
            return;
        }
        needExit = true;
        Toast.makeText(this, "再按一次返回键退出!", Toast.LENGTH_SHORT).show();
    }

//    // 在发生物理按键动作时触发
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) { // 按下返回键
//            if (needExit) {
//                finish(); // 关闭当前页面
//            }
//            needExit = true;
//            Toast.makeText(this, "再按一次返回键退出!", Toast.LENGTH_SHORT).show();
//            return true;
//        } else {
//            return super.onKeyDown(keyCode, event);
//        }
//    }

}