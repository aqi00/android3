package com.example.chapter04;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter04.receiver.ShockReceiver;

public class BroadStaticActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broad_static);
        findViewById(R.id.btn_send_shock).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send_shock) {
            // Android8.0之后删除了大部分静态注册，防止退出App后仍在接收广播，
            // 为了让应用能够继续接收静态广播，需要给静态注册的广播指定包名。
            String receiverPath = "com.example.chapter04.receiver.ShockReceiver";
            Intent intent = new Intent(ShockReceiver.SHOCK_ACTION); // 创建一个指定动作的意图
            // 发送静态广播之时，需要通过setComponent方法指定接收器的完整路径
            ComponentName componentName  = new ComponentName(this, receiverPath);
            intent.setComponent(componentName); // 设置意图的组件信息
            sendBroadcast(intent); // 发送静态广播
            Toast.makeText(this, "已发送震动广播", Toast.LENGTH_SHORT).show();
        }
    }
}
