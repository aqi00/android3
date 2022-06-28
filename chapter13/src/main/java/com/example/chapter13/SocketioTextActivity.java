package com.example.chapter13;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter13.constant.NetConst;
import com.example.chapter13.util.DateUtil;
import com.example.chapter13.util.SocketUtil;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketioTextActivity extends AppCompatActivity {
    private static final String TAG = "SocketioTextActivity";
    private EditText et_input; // 声明一个编辑框对象
    private TextView tv_response; // 声明一个文本视图对象
    private Socket mSocket; // 声明一个套接字对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socketio_text);
        et_input = findViewById(R.id.et_input);
        tv_response = findViewById(R.id.tv_response);
        findViewById(R.id.btn_send).setOnClickListener(v -> {
            String content = et_input.getText().toString();
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this, "请输入聊天消息", Toast.LENGTH_SHORT).show();
                return;
            }
            mSocket.emit("send_text", content); // 往Socket服务器发送文本消息
        });
        initSocket(); // 初始化套接字
    }

    // 初始化套接字
    private void initSocket() {
        // 检查能否连上Socket服务器
        SocketUtil.checkSocketAvailable(this, NetConst.BASE_IP, NetConst.BASE_PORT);
        try {
            String uri = String.format("http://%s:%d/", NetConst.BASE_IP, NetConst.BASE_PORT);
            mSocket = IO.socket(uri); // 创建指定地址和端口的套接字实例
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mSocket.connect(); // 建立Socket连接
        // 等待接收传来的文本消息
        mSocket.on("receive_text", (args) -> {
            String desc = String.format("%s 收到服务端消息：%s",
                    DateUtil.getNowTime(), (String) args[0]);
            runOnUiThread(() -> tv_response.setText(desc));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("receive_text"); // 取消接收传来的文本消息
        if (mSocket.connected()) { // 已经连上Socket服务器
            mSocket.disconnect(); // 断开Socket连接
        }
        mSocket.close(); // 关闭Socket连接
    }
}