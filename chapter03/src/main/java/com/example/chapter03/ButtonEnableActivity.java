package com.example.chapter03;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chapter03.util.DateUtil;

// 活动类直接实现点击监听器的接口View.OnClickListener
public class ButtonEnableActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_result; // 声明一个文本视图实例
    private Button btn_test; // 声明一个按钮控件实例

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_enable);
        tv_result = findViewById(R.id.tv_result); // 获取名叫tv_result的文本视图
        // 因为按钮控件的setOnClickListener方法来自View基类，所以也可对findViewById得到的视图直接设置点击监听器
        findViewById(R.id.btn_enable).setOnClickListener(this);
        findViewById(R.id.btn_disable).setOnClickListener(this);
        btn_test = findViewById(R.id.btn_test); // 获取名叫btn_test的按钮控件
        btn_test.setOnClickListener(this); // 设置btn_test的点击监听器
    }

    @Override
    public void onClick(View v) { // 点击事件的处理方法
        // 由于多个控件都把点击监听器设置到了当前页面，因此公共的onClick方法内部需要区分来自哪个按钮
        if (v.getId() == R.id.btn_enable) { // 点击了按钮“启用测试按钮”
            btn_test.setTextColor(Color.BLACK); // 设置按钮的文字颜色
            btn_test.setEnabled(true); // 启用当前控件
        } else if (v.getId() == R.id.btn_disable) { // 点击了按钮“禁用测试按钮”
            btn_test.setTextColor(Color.GRAY); // 设置按钮的文字颜色
            btn_test.setEnabled(false); // 禁用当前控件
        } else if (v.getId() == R.id.btn_test) { // 点击了按钮“测试按钮”
            String desc = String.format("%s 您点击了按钮：%s",
                    DateUtil.getNowTime(), ((Button) v).getText());
            tv_result.setText(desc); // 设置文本视图的文本内容
        }
    }
}
