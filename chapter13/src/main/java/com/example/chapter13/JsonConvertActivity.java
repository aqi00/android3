package com.example.chapter13;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter13.bean.UserInfo;
import com.google.gson.Gson;

@SuppressLint(value={"DefaultLocale","SetTextI18n"})
public class JsonConvertActivity extends AppCompatActivity {
    private TextView tv_json; // 声明一个文本视图对象
    private UserInfo mUser; // 声明一个用户信息对象
    private String mJsonStr; // JSON格式的字符串

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_convert);
        mUser = new UserInfo("阿四", 25, 165L, 50.0f); // 创建用户实例
        mJsonStr = new Gson().toJson(mUser); // 把用户实例转换为JSON串
        tv_json = findViewById(R.id.tv_json);
        findViewById(R.id.btn_origin_json).setOnClickListener(v -> {
            mJsonStr = new Gson().toJson(mUser); // 把用户实例转换为JSON字符串
            tv_json.setText("JSON串内容如下：\n" + mJsonStr);
        });
        findViewById(R.id.btn_convert_json).setOnClickListener(v -> {
            // 把JSON串转换为UserInfo类型的对象
            UserInfo newUser = new Gson().fromJson(mJsonStr, UserInfo.class);
            String desc = String.format("\n\t姓名=%s\n\t年龄=%d\n\t身高=%d\n\t体重=%f",
                    newUser.name, newUser.age, newUser.height, newUser.weight);
            tv_json.setText("从JSON串解析而来的用户信息如下：" + desc);
        });
    }

}
