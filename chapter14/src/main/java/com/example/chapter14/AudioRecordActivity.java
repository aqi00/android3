package com.example.chapter14;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chapter14.util.DateUtil;
import com.example.chapter14.util.FileUtil;

public class AudioRecordActivity extends AppCompatActivity {
    private final static String TAG = "AudioRecordActivity";
    private int RECORDER_CODE = 1; // 录制操作的请求码
    private TextView tv_audio;
    private ImageView iv_audio; // 该图标充当播放按钮
    private Uri mAudioUri; // 音频文件的uri路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);
        tv_audio = findViewById(R.id.tv_audio);
        iv_audio = findViewById(R.id.iv_audio);
        findViewById(R.id.btn_recorder).setOnClickListener(v -> {
            // 下面打开系统自带的录音机
            Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            startActivityForResult(intent, RECORDER_CODE); // 跳到录音机页面
        });
        findViewById(R.id.iv_audio).setOnClickListener(v -> {
            // 下面打开系统自带的收音机
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(mAudioUri, "audio/*"); // 类型为音频
            startActivity(intent); // 跳到收音机页面
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode==RESULT_OK && requestCode==RECORDER_CODE){
            mAudioUri = intent.getData(); // 获得录制好的音频uri
            String filePath = String.format("%s/%s.mp3",
                    getExternalFilesDir(Environment.DIRECTORY_MUSIC), "audio_"+ DateUtil.getNowDateTime());
            FileUtil.saveFileFromUri(this, mAudioUri, filePath); // 保存为临时文件
            tv_audio.setText("录制完成的音频地址为："+mAudioUri.toString());
            iv_audio.setVisibility(View.VISIBLE);
        }
    }

}
