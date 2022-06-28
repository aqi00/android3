package com.example.chapter15;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("SetTextI18n")
public class JniCpuActivity extends AppCompatActivity {
    private TextView tv_cpu_jni; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jni_cpu);
        TextView tv_cpu_build = findViewById(R.id.tv_cpu_build);
        tv_cpu_build.setText("Build类获得的CPU指令集为" + Build.CPU_ABI);
        tv_cpu_jni = findViewById(R.id.tv_cpu_jni);
        findViewById(R.id.btn_cpu).setOnClickListener(v -> {
            // 调用JNI方法cpuFromJNI获得CPU信息
            String desc = cpuFromJNI(1, 0.5f, 99.9, true);
            tv_cpu_jni.setText(desc);
        });
    }

    // 声明cpuFromJNI是来自JNI的原生方法
    public native String cpuFromJNI(int i1, float f1, double d1, boolean b1);

    // 在加载当前类时就去加载libcommon.so，加载动作发生在页面启动之前
    static {
        System.loadLibrary("common");
    }

}
