package com.example.chapter19;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.huawei.hms.hmsscankit.ScanUtil;

public class RecognizeResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize_result);
        TextView tv_result = findViewById(R.id.tv_result);
        String recognizeResult = getIntent().getStringExtra(ScanUtil.RESULT);
        tv_result.setText("识别结果文本为：\n"+recognizeResult);
    }
}