package com.example.chapter05;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditFocusActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {
    private EditText et_phone; // 声明一个编辑框对象
    private EditText et_password; // 声明一个编辑框对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_focus);
        // 从布局文件中获取名叫et_phone的手机号码编辑框
        et_phone = findViewById(R.id.et_phone);
        // 从布局文件中获取名叫et_password的密码编辑框
        et_password = findViewById(R.id.et_password);
        // 给密码编辑框注册点击事件监听器
        et_password.setOnClickListener(this);
        // 给密码编辑框注册一个焦点变化监听器，一旦焦点发生变化，就触发监听器的onFocusChange方法
        et_password.setOnFocusChangeListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    // 焦点变更事件的处理方法，hasFocus表示当前控件是否获得焦点。
    // 为什么光标进入事件不选onClick？因为要点两下才会触发onClick动作（第一下是切换焦点动作）
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // 判断密码编辑框是否获得焦点。hasFocus为true表示获得焦点，为false表示失去焦点
        if (v.getId()==R.id.et_password && hasFocus) {
            String phone = et_phone.getText().toString();
            if (TextUtils.isEmpty(phone) || phone.length()<11) { // 手机号码不足11位
                // 手机号码编辑框请求焦点，也就是把光标移回手机号码编辑框
                et_phone.requestFocus();
                Toast.makeText(this, "请输入11位手机号码", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        // 编辑框比较特殊，要点击两次后才会触发点击事件，因为第一次点击只触发焦点变更事件，第二次点击才触发点击事件
        if (v.getId() == R.id.et_password) {
            String phone = et_phone.getText().toString();
            if (TextUtils.isEmpty(phone) || phone.length()<11) { // 手机号码不足11位
                // 手机号码编辑框请求焦点，也就是把光标移回手机号码编辑框
                et_phone.requestFocus();
                Toast.makeText(this, "请输入11位手机号码", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.btn_login) {
            String password = et_password.getText().toString();
            if (TextUtils.isEmpty(password) || password.length()<11) { // 密码不足6位
                // 密码编辑框请求焦点，也就是把光标移回密码编辑框
                et_password.requestFocus();
                Toast.makeText(this, "请输入6位密码", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
