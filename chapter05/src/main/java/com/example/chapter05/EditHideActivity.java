package com.example.chapter05;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter05.util.ViewUtil;

public class EditHideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hide);
        // 从布局文件中获取名叫et_phone的手机号码编辑框
        EditText et_phone = findViewById(R.id.et_phone);
        // 从布局文件中获取名叫et_password的密码编辑框
        EditText et_password = findViewById(R.id.et_password);
        // 给手机号码编辑框添加文本变化监听器
        et_phone.addTextChangedListener(new HideTextWatcher(et_phone, 11));
        // 给密码编辑框添加文本变化监听器
        et_password.addTextChangedListener(new HideTextWatcher(et_password, 6));
    }

    // 定义一个编辑框监听器，在输入文本达到指定长度时自动隐藏输入法
    private class HideTextWatcher implements TextWatcher {
        private EditText mView; // 声明一个编辑框对象
        private int mMaxLength; // 声明一个最大长度变量

        public HideTextWatcher(EditText v, int maxLength) {
            super();
            mView = v;
            mMaxLength = maxLength;
        }

        // 在编辑框的输入文本变化前触发
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        // 在编辑框的输入文本变化时触发
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        // 在编辑框的输入文本变化后触发
        public void afterTextChanged(Editable s) {
            String str = s.toString(); // 获得已输入的文本字符串
            // 输入文本达到11位（如手机号码），或者达到6位（如登录密码）时关闭输入法
            if ((str.length() == 11 && mMaxLength == 11)
                || (str.length() == 6 && mMaxLength == 6)) {
                ViewUtil.hideOneInputMethod(EditHideActivity.this, mView); // 隐藏输入法软键盘
            }
        }
    }
}
