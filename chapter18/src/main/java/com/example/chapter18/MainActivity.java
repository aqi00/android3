package com.example.chapter18;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chapter18.util.PermissionUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_speech_engine).setOnClickListener(this);
        findViewById(R.id.btn_speech_compose).setOnClickListener(this);
        findViewById(R.id.btn_pinyin).setOnClickListener(this);
        findViewById(R.id.btn_audio_raw).setOnClickListener(this);
        findViewById(R.id.btn_voice_compose).setOnClickListener(this);
        findViewById(R.id.btn_voice_recognize).setOnClickListener(this);
        findViewById(R.id.btn_voice_inference).setOnClickListener(this);
        findViewById(R.id.btn_robot).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_speech_engine) {
            startActivity(new Intent(this, SpeechEngineActivity.class));
        } else if (v.getId() == R.id.btn_speech_compose) {
            startActivity(new Intent(this, SpeechComposeActivity.class));
        } else if (v.getId() == R.id.btn_pinyin) {
            startActivity(new Intent(this, PinyinActivity.class));
        } else if (v.getId() == R.id.btn_audio_raw) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.RECORD_AUDIO}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, AudioRawActivity.class));
            }
        } else if (v.getId() == R.id.btn_voice_compose) {
            startActivity(new Intent(this, VoiceComposeActivity.class));
        } else if (v.getId() == R.id.btn_voice_recognize) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.RECORD_AUDIO}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, VoiceRecognizeActivity.class));
            }
        } else if (v.getId() == R.id.btn_voice_inference) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.RECORD_AUDIO}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, VoiceInferenceActivity.class));
            }
        } else if (v.getId() == R.id.btn_robot) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, RobotActivity.class));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // requestCode不能为负数，也不能大于2的16次方即65536
        if (requestCode == R.id.btn_audio_raw % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, AudioRawActivity.class));
            } else {
                Toast.makeText(this, "需要允许录音权限才能录制音频噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_voice_recognize % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, VoiceRecognizeActivity.class));
            } else {
                Toast.makeText(this, "需要允许录音权限才能识别语音噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_voice_inference % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, VoiceInferenceActivity.class));
            } else {
                Toast.makeText(this, "需要允许录音权限才能推断语音噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_robot % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, RobotActivity.class));
            } else {
                Toast.makeText(this, "需要允许录音和定位权限才能使用语音机器人噢", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
