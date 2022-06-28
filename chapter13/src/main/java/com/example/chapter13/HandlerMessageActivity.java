package com.example.chapter13;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter13.util.DateUtil;

import java.util.Random;

@SuppressLint("HandlerLeak")
public class HandlerMessageActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_message; // 声明一个文本视图对象
    private boolean isPlaying = false; // 是否正在播放新闻
    private int BEGIN = 0, SCROLL = 1, END = 2; // 0为开始，1为滚动，2为结束
    private String[] mNewsArray = { "北斗导航系统正式开通，定位精度媲美GPS",
            "黑人之死引发美国各地反种族主义运动", "印度运营商禁止华为中兴反遭诺基亚催债",
            "贝鲁特发生大爆炸全球紧急救援黎巴嫩", "日本货轮触礁毛里求斯造成严重漏油污染"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_message);
        tv_message = findViewById(R.id.tv_message);
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start) { // 点击了开始播放新闻的按钮
            if (!isPlaying) { // 如果不在播放就开始播放
                isPlaying = true;
                new PlayThread().start(); // 创建并启动新闻播放线程
            }
        } else if (v.getId() == R.id.btn_stop) { // 点击了结束播放新闻的按钮
            isPlaying = false;
        }
    }

    // 定义一个新闻播放线程
    private class PlayThread extends Thread {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(BEGIN); // 向处理器发送播放开始的空消息
            while (isPlaying) { // 正在播放新闻
                try {
                    sleep(2000); // 睡眠两秒（2000毫秒）
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = Message.obtain(); // 获得默认的消息对象
                //Message message = mHandler.obtainMessage(); // 获得处理器的消息对象
                message.what = SCROLL; // 消息类型
                message.obj = mNewsArray[new Random().nextInt(5)]; // 消息描述
                mHandler.sendMessage(message); // 向处理器发送消息
            }
            mHandler.sendEmptyMessage(END); // 向处理器发送播放结束的空消息
            // 如果只要简单处理，也可绕过Handler，直接调用runOnUiThread方法操作界面
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    String desc = String.format("%s\n%s %s", tv_message.getText().toString(), DateUtil.getNowTime(), "新闻播放结束，谢谢观看");
//                    tv_message.setText(desc);
//                }
//            });
            isPlaying = false;
        }
    }

    // 创建一个处理器对象
    private Handler mHandler = new Handler(Looper.myLooper()) {
        // 在收到消息时触发
        public void handleMessage(Message msg) {
            String desc = tv_message.getText().toString();
            if (msg.what == BEGIN) { // 开始播放
                desc = String.format("%s\n%s %s", desc, DateUtil.getNowTime(), "开始播放新闻");
            } else if (msg.what == SCROLL) { // 滚动播放
                desc = String.format("%s\n%s %s", desc, DateUtil.getNowTime(), msg.obj);
            } else if (msg.what == END) { // 结束播放
                desc = String.format("%s\n%s %s", desc, DateUtil.getNowTime(), "新闻播放结束");
            }
            tv_message.setText(desc);
        }
    };

}
