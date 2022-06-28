package com.example.chapter13;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter13.util.DateUtil;

import java.util.Random;

public class ThreadUiActivity extends AppCompatActivity {
    private TextView tv_message; // 声明一个文本视图对象
    private boolean isPlaying = false; // 是否正在播放新闻
    private String[] mNewsArray = { "北斗导航系统正式开通，定位精度媲美GPS",
            "黑人之死引发美国各地反种族主义运动", "印度运营商禁止华为中兴反遭诺基亚催债",
            "贝鲁特发生大爆炸全球紧急救援黎巴嫩", "日本货轮触礁毛里求斯造成严重漏油污染"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_ui);
        tv_message = findViewById(R.id.tv_message);
        findViewById(R.id.btn_start).setOnClickListener(v -> {
            if (!isPlaying) { // 如果不在播放就开始播放
                isPlaying = true;
                new Thread(() -> broadcastNews()).start(); // 启动新闻播放线程
            }
        });
        findViewById(R.id.btn_stop).setOnClickListener(v -> isPlaying = false);
    }

    // 播放新闻
    private void broadcastNews() {
        // 回到主线程（UI线程）操纵界面
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                String desc = String.format("%s\n%s %s", tv_message.getText().toString(),
//                        DateUtil.getNowTime(), "开始播放新闻");
//                tv_message.setText(desc);
//            }
//        });
        String startDesc = String.format("%s\n%s %s", tv_message.getText().toString(),
                DateUtil.getNowTime(), "开始播放新闻");
        // 回到主线程（UI线程）操纵界面
        runOnUiThread(() -> tv_message.setText(startDesc));
        while (isPlaying) { // 正在播放新闻
            try {
                Thread.sleep(2000); // 睡眠两秒（2000毫秒）
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String runDesc = String.format("%s\n%s %s", tv_message.getText().toString(),
                    DateUtil.getNowTime(), mNewsArray[new Random().nextInt(5)]);
            // 回到主线程（UI线程）操纵界面
            runOnUiThread(() -> tv_message.setText(runDesc));
        }
        String endDesc = String.format("%s\n%s %s", tv_message.getText().toString(),
                DateUtil.getNowTime(), "新闻播放结束，谢谢观看");
        // 回到主线程（UI线程）操纵界面
        runOnUiThread(() -> tv_message.setText(endDesc));
        isPlaying = false;
    }

}