package com.example.chapter08;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter08.service.MusicService;

public class ForegroundServiceActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_song; // 声明一个编辑框对象
    private Button btn_send_service; // 声明一个按钮对象
    private boolean isPlaying = true; // 是否正在播放

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreground_service);
        et_song = findViewById(R.id.et_song);
        btn_send_service = findViewById(R.id.btn_send_service);
        btn_send_service.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send_service) {
            if (TextUtils.isEmpty(et_song.getText())) {
                Toast.makeText(this, "请填写歌曲名称", Toast.LENGTH_SHORT).show();
                return;
            }
            // 创建一个通往音乐服务的意图
            Intent intent = new Intent(this, MusicService.class);
            intent.putExtra("is_play", isPlaying); // 是否正在播放音乐
            intent.putExtra("song", et_song.getText().toString());
            btn_send_service.setText(isPlaying?"暂停播放音乐":"开始播放音乐");
            startService(intent); // 启动音乐播放服务
            isPlaying = !isPlaying;
        }
    }

}
