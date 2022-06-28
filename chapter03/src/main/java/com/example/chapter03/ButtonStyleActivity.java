package com.example.chapter03;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chapter03.util.DateUtil;

public class ButtonStyleActivity extends AppCompatActivity {
    private TextView tv_result; // 声明一个文本视图实例

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_style);
        tv_result = findViewById(R.id.tv_result); // 获取名叫tv_result的文本视图
    }

    // activity_button_style.xml中给btn_click_xml指定了点击方法doClick
    public void doClick(View view) {
        String desc = String.format("%s 您点击了按钮：%s",
                DateUtil.getNowTime(), ((Button) view).getText());
        tv_result.setText(desc); // 设置文本视图的文本内容
    }

}
