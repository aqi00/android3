package com.example.chapter05;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SwitchIOSActivity extends AppCompatActivity implements OnCheckedChangeListener {
    private CheckBox ck_status; // 声明一个复选框对象
    private TextView tv_result; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_ios);
        // 从布局文件中获取名叫sw_status的开关按钮
        ck_status = findViewById(R.id.ck_status);
        // 从布局文件中获取名叫tv_result的文本视图
        tv_result = findViewById(R.id.tv_result);
        // 给开关按钮设置选择监听器，一旦用户点击它，就触发监听器的onCheckedChanged方法
        ck_status.setOnCheckedChangeListener(this);
        refreshResult(); // 刷新仿iOS按钮的开关说明
    }

    // 刷新仿iOS按钮的开关说明
    private void refreshResult() {
        String result = String.format("仿iOS开关的状态是%s",
                (ck_status.isChecked()) ? "开" : "关");
        tv_result.setText(result);
    }

    // 选择事件的处理方法
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        refreshResult();
    }

}
