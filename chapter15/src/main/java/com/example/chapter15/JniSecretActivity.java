package com.example.chapter15;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class JniSecretActivity extends AppCompatActivity {
    private EditText et_origin; // 声明一个用于输入原始字符串的编辑框对象
    private TextView tv_encrypt; // 声明一个文本视图对象
    private TextView tv_decrypt; // 声明一个文本视图对象
    private String mKey = "123456789abcdef"; // 该算法要求密钥串的长度为16位
    private String mEncrypt; // 加密串

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jni_secret);
        et_origin = findViewById(R.id.et_origin);
        tv_encrypt = findViewById(R.id.tv_encrypt);
        tv_decrypt = findViewById(R.id.tv_decrypt);
        findViewById(R.id.btn_encrypt).setOnClickListener(v -> {
            // 调用JNI方法encryptFromJNI获得加密后的字符串
            mEncrypt = encryptFromJNI(et_origin.getText().toString(), mKey);
            tv_encrypt.setText("jni加密结果为："+mEncrypt);
        });
        findViewById(R.id.btn_decrypt).setOnClickListener(v -> {
            if (TextUtils.isEmpty(mEncrypt)) {
                Toast.makeText(this, "请先加密后再解密", Toast.LENGTH_SHORT).show();
                return;
            }
            // 调用JNI方法decryptFromJNI获得解密后的字符串
            String raw = decryptFromJNI(mEncrypt, mKey);
            tv_decrypt.setText("jni解密结果为："+raw);
        });
    }

    // 声明encryptFromJNI是来自JNI的原生方法
    public native String encryptFromJNI(String raw, String key);

    // 声明decryptFromJNI是来自JNI的原生方法
    public native String decryptFromJNI(String des, String key);

    // 在加载当前类时就去加载libcommon.so，加载动作发生在页面启动之前
    static {
        System.loadLibrary("common");
    }

}
