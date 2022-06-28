package com.example.chapter13;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter13.constant.NetConst;
import com.example.chapter13.task.AppClientEndpoint;
import com.example.chapter13.util.DateUtil;

import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

public class WebSocketActivity extends AppCompatActivity {
    private static final String TAG = "WebSocketActivity";
    private static final String SERVER_URL = NetConst.WEBSOCKET_PREFIX + "testWebSocket";
    private EditText et_input; // 声明一个编辑框对象
    private TextView tv_response; // 声明一个文本视图对象
    private AppClientEndpoint mAppTask; // 声明一个WebSocket客户端任务对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_socket);
        et_input = findViewById(R.id.et_input);
        tv_response = findViewById(R.id.tv_response);
        findViewById(R.id.btn_send).setOnClickListener(v -> {
            String content = et_input.getText().toString();
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(this, "请输入消息文本", Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(() -> mAppTask.sendRequest(content)).start(); // 启动线程发送文本消息
        });
        new Thread(() -> initWebSocket()).start(); // 启动线程初始化WebSocket客户端
    }

    // 初始化WebSocket的客户端任务
    private void initWebSocket() {
        // 创建文本传输任务，并指定消息应答监听器
        mAppTask = new AppClientEndpoint(this, resp -> {
            String desc = String.format("%s 收到服务端返回：%s",
                    DateUtil.getNowTime(), resp);
            tv_response.setText(desc);
        });
        // 获取WebSocket容器
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            URI uri = new URI(SERVER_URL); // 创建一个URI对象
            // 连接WebSocket服务器，并关联文本传输任务获得连接会话
            Session session = container.connectToServer(mAppTask, uri);
            // 设置文本消息的最大缓存大小
            session.setMaxTextMessageBufferSize(1024 * 1024 * 10);
            // 设置二进制消息的最大缓存大小
            //session.setMaxBinaryMessageBufferSize(1024 * 1024 * 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}