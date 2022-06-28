package com.example.chapter18;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chapter18.constant.SoundConstant;
import com.example.chapter18.task.TtsClientEndpoint;
import com.example.chapter18.util.DateUtil;
import com.example.chapter18.util.SoundUtil;

public class VoiceComposeActivity extends AppCompatActivity {
    private final static String TAG = "VoiceComposeActivity";
    private TextView tv_option; // 声明一个文本视图对象
    private EditText et_compose_text; // 声明一个编辑框对象
    private TextView tv_result; // 声明一个文本视图对象
    private String mComposeFilePath; // 合成语音的文件路径
    private MediaPlayer mMediaPlayer = new MediaPlayer(); // 媒体播放器
    private boolean isPlaying = false; // 是否正在播音

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_compose);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("在线语音合成");
        tv_option = findViewById(R.id.tv_option);
        tv_option.setText("开始播放语音");
        tv_option.setVisibility(View.GONE);
        et_compose_text = findViewById(R.id.et_compose_text);
        tv_result = findViewById(R.id.tv_result);
        findViewById(R.id.btn_compose_voice).setOnClickListener(v -> {
            String text = et_compose_text.getText().toString();
            if (TextUtils.isEmpty(text)) {
                Toast.makeText(this, "请先输入待朗读的一段话", Toast.LENGTH_SHORT).show();
                return;
            }
            new Thread(() -> onlineCompose(text)).start(); // 启动在线合成语音的线程
        });
        tv_option.setOnClickListener(v -> {
            if (!isPlaying) { // 未在播音
                startPlay(); // 开始播音
            } else { // 正在播音
                stopPlay(); // 停止播音
            }
        });
    }

    // 在线合成语音
    private void onlineCompose(String text) {
        mComposeFilePath = String.format("%s/%s.mp3",
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),
                DateUtil.getNowDateTime());
        // 创建语音合成任务，并指定语音监听器
        TtsClientEndpoint task = new TtsClientEndpoint(this, mComposeFilePath, text, arg -> {
            if (Boolean.TRUE.equals(arg[0])) {
                Toast.makeText(this, "语音合成结束", Toast.LENGTH_SHORT).show();
                tv_result.setText("音频文件位于"+arg[2]);
                tv_option.setVisibility(View.VISIBLE);
            }
        });
        SoundUtil.startSoundTask(SoundConstant.URL_TTS, task); // 启动语音合成任务
    }

    // 开始播音
    private void startPlay() {
        isPlaying = !isPlaying;
        tv_option.setText("停止播放语音");
        mMediaPlayer.reset(); // 重置媒体播放器
        // 设置媒体播放器的完成监听器
        mMediaPlayer.setOnCompletionListener(mp -> stopPlay());
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // 设置音频流的类型为音乐
        try {
            mMediaPlayer.setDataSource(mComposeFilePath); // 设置媒体数据的文件路径
            mMediaPlayer.prepare(); // 媒体播放器准备就绪
            mMediaPlayer.start(); // 媒体播放器开始播放
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 停止播音
    private void stopPlay() {
        tv_option.setText("开始播放语音");
        if (mMediaPlayer.isPlaying() || isPlaying) { // 如果正在播放
            isPlaying = !isPlaying;
            mMediaPlayer.stop(); // 停止播放
            Toast.makeText(this, "语音播放结束", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlay(); // 停止播音
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release(); // 释放媒体播放器
    }
}
