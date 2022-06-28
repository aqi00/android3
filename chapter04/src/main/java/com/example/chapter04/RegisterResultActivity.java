package com.example.chapter04;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.chapter04.util.DateUtil;

public class RegisterResultActivity extends AppCompatActivity implements View.OnClickListener {
    private String mRrequest = "你吃饭了吗？来我家吃吧";
    private TextView tv_response; // 声明一个文本视图对象
    private ActivityResultLauncher mLauncher; // 声明一个活动结果启动器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_result);
        // 从布局文件中获取名叫tv_request的文本视图
        TextView tv_request = findViewById(R.id.tv_request);
        tv_request.setText("待发送的消息为："+mRrequest);
        // 从布局文件中获取名叫tv_response的文本视图
        tv_response = findViewById(R.id.tv_response);
        findViewById(R.id.btn_request).setOnClickListener(this);
        // 注册一个善后工作的活动结果启动器
        mLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode()==RESULT_OK && result.getData()!=null) {
                Bundle bundle = result.getData().getExtras(); // 从返回的意图中获取快递包裹
                // 从包裹中取出名叫response_time的字符串
                String response_time = bundle.getString("response_time");
                // 从包裹中取出名叫response_content的字符串
                String response_content = bundle.getString("response_content");
                String desc = String.format("收到返回消息：\n应答时间为：%s\n应答内容为：%s",
                        response_time, response_content);
                tv_response.setText(desc); // 把返回消息的详情显示在文本视图上
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_request) {
            // 创建一个意图对象，准备跳到指定的活动页面
            Intent intent = new Intent(this, ActResponseActivity.class);
            Bundle bundle = new Bundle(); // 创建一个新包裹
            // 往包裹存入名叫request_time的字符串
            bundle.putString("request_time", DateUtil.getNowTime());
            // 往包裹存入名叫request_content的字符串
            bundle.putString("request_content", mRrequest);
            intent.putExtras(bundle); // 把快递包裹塞给意图
            mLauncher.launch(intent); // 活动结果启动器开动了
        }
    }

}