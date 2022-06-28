package com.example.chapter03;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chapter03.util.DateUtil;

public class ButtonLongclickActivity extends AppCompatActivity implements View.OnLongClickListener {
    private TextView tv_result; // 声明一个文本视图实例

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_longclick);
        tv_result = findViewById(R.id.tv_result); // 获取名叫tv_result的文本视图
        // 从布局文件中获取名叫btn_click_single的按钮控件
        Button btn_longclick_single = findViewById(R.id.btn_longclick_single);
        // 设置长按监听器，一旦用户长按按钮，就触发监听器的onLongClick方法
        btn_longclick_single.setOnLongClickListener(new MyOnLongClickListener());
        // 从布局文件中获取名叫btn_click_public的按钮控件
        Button btn_longclick_public = findViewById(R.id.btn_longclick_public);
        // 设置长按监听器，一旦用户长按按钮，就触发监听器的onLongClick方法
        btn_longclick_public.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) { // 长按事件的处理方法
        if (v.getId() == R.id.btn_longclick_public) { // 来自按钮btn_longclick_public
            String desc = String.format("%s 您长按了按钮：%s",
                    DateUtil.getNowTime(), ((Button) v).getText());
            tv_result.setText(desc); // 设置文本视图的文本内容
        }
        return true;
    }

    // 定义一个长按监听器，它实现了接口View.OnLongClickListener
    class MyOnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) { // 长按事件的处理方法
            String desc = String.format("%s 您长按了按钮：%s",
                    DateUtil.getNowTime(), ((Button) v).getText());
            tv_result.setText(desc); // 设置文本视图的文本内容
            return true;
        }
    }
}
