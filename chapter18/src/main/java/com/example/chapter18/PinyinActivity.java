package com.example.chapter18;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chapter18.util.PinyinUtil;

public class PinyinActivity extends AppCompatActivity {
    private final static String TAG = "PinyinActivity";
    private EditText et_hanzi; // 声明一个编辑框对象
    private CheckBox ck_tone; // 声明一个复选框对象
    private TextView tv_pinyin; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinyin);
        et_hanzi = findViewById(R.id.et_hanzi);
        ck_tone = findViewById(R.id.ck_tone);
        tv_pinyin = findViewById(R.id.tv_pinyin);
        findViewById(R.id.btn_convert).setOnClickListener(v -> showPinyin());
        ck_tone.setOnCheckedChangeListener((buttonView, isChecked) -> showPinyin());
    }

    // 显示转换后的汉字拼音
    private void showPinyin() {
        String hanzi = et_hanzi.getText().toString();
        if (TextUtils.isEmpty(hanzi)) {
            Toast.makeText(this, "请先输入待转换的汉字", Toast.LENGTH_SHORT).show();
            return;
        }
        // 把汉字串转为拼音串
        String pinyin = PinyinUtil.getHanziPinYin(hanzi, ck_tone.isChecked());
        tv_pinyin.setText(pinyin);
    }
}