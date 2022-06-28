package com.example.chapter18;

import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter18.task.AudioPlayTask;
import com.example.chapter18.task.AudioRecordTask;
import com.example.chapter18.util.DateUtil;

public class AudioRawActivity extends AppCompatActivity implements
        OnCheckedChangeListener, AudioRecordTask.OnRecordListener, AudioPlayTask.OnPlayListener {
    private static final String TAG = "AudioRawActivity";
    private TextView tv_audio_record; // 声明一个文本视图对象
    private CheckBox ck_audio_record; // 声明一个复选框对象
    private TextView tv_audio_play; // 声明一个文本视图对象
    private CheckBox ck_audio_play; // 声明一个复选框对象

    private int mFrequence; // 音频的采样频率
    private int mInChannel; // 音频的声道类型（录音时候）
    private int mOutChannel; // 音频的声道类型（播音时候）
    private int mFormat; // 音频的编码格式
    private String mRecordFilePath; // 录制文件的保存路径
    private AudioRecordTask mRecordTask; // 声明一个原始音频录制线程对象
    private AudioPlayTask mPlayTask; // 声明一个原始音频播放线程对象

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_raw);
        tv_audio_record = findViewById(R.id.tv_audio_record);
        ck_audio_record = findViewById(R.id.ck_audio_record);
        ck_audio_record.setOnCheckedChangeListener(this);
        tv_audio_play = findViewById(R.id.tv_audio_play);
        ck_audio_play = findViewById(R.id.ck_audio_play);
        ck_audio_play.setOnCheckedChangeListener(this);
        initFrequenceSpinner(); // 初始化采样频率的下拉框
        initChannelSpinner(); // 初始化声道类型的下拉框
        initFormatSpinner(); // 初始化编码格式的下拉框
    }

    // 初始化采样频率的下拉框
    private void initFrequenceSpinner() {
        ArrayAdapter<String> frequenceAdapter = new ArrayAdapter<>(this,
                R.layout.item_select, frequenceDescArray);
        Spinner sp_frequence = findViewById(R.id.sp_frequence);
        sp_frequence.setPrompt("请选择采样频率");
        sp_frequence.setAdapter(frequenceAdapter);
        sp_frequence.setOnItemSelectedListener(new FrequenceSelectedListener());
        sp_frequence.setSelection(0);
    }

    private String[] frequenceDescArray = {"16000赫兹", "8000赫兹"};
    private int[] frequenceArray = {16000, 8000};

    class FrequenceSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mFrequence = frequenceArray[arg2];
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 初始化声道类型的下拉框
    private void initChannelSpinner() {
        ArrayAdapter<String> channelAdapter = new ArrayAdapter<>(this,
                R.layout.item_select, channelDescArray);
        Spinner sp_channel = findViewById(R.id.sp_channel);
        sp_channel.setPrompt("请选择声道类型");
        sp_channel.setAdapter(channelAdapter);
        sp_channel.setSelection(0);
        sp_channel.setOnItemSelectedListener(new ChannelSelectedListener());
    }

    private String[] channelDescArray = {"单声道", "立体声"};
    private int[] inChannelArray = {AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO};
    private int[] outChannelArray = {AudioFormat.CHANNEL_OUT_MONO, AudioFormat.CHANNEL_OUT_STEREO};
    class ChannelSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mInChannel = inChannelArray[arg2];
            mOutChannel = outChannelArray[arg2];
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 初始化编码格式的下拉框
    private void initFormatSpinner() {
        ArrayAdapter<String> formatAdapter = new ArrayAdapter<>(this,
                R.layout.item_select, formatDescArray);
        Spinner sp_format = findViewById(R.id.sp_format);
        sp_format.setPrompt("请选择编码格式");
        sp_format.setAdapter(formatAdapter);
        sp_format.setSelection(0);
        sp_format.setOnItemSelectedListener(new FormatSelectedListener());
    }

    private String[] formatDescArray = {"16位", "8位"};
    private int[] formatArray = {AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT};
    class FormatSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mFormat = formatArray[arg2];
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_audio_record) {
            if (isChecked) { // 开始录音
                // 生成原始音频的文件路径
                mRecordFilePath = String.format("%s/%s.pcm",
                        getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),
                        DateUtil.getNowDateTime());
                ck_audio_record.setText("停止录音");
                int[] params = new int[] {mFrequence, mInChannel, mFormat};
                // 创建一个原始音频录制线程，并设置录制事件监听器
                mRecordTask = new AudioRecordTask(this, mRecordFilePath, params, this);
                mRecordTask.start(); // 启动原始音频录制线程
            } else { // 停止录音
                ck_audio_record.setText("开始录音");
                mRecordTask.cancel(); // 原始音频录制线程取消录音
                ck_audio_play.setVisibility(View.VISIBLE);
            }
        } else if (buttonView.getId() == R.id.ck_audio_play) {
            if (isChecked) { // 开始播音
                ck_audio_play.setText("暂停播音");
                int[] params = new int[] {mFrequence, mOutChannel, mFormat};
                // 创建一个原始音频播放线程，并设置播放事件监听器
                mPlayTask = new AudioPlayTask(this, mRecordFilePath, params, this);
                mPlayTask.start(); // 启动原始音频播放线程
            } else { // 停止播音
                ck_audio_play.setText("开始播音");
                mPlayTask.cancel(); // 原始音频播放线程取消播音
            }
        }
    }

    // 在录音进度更新时触发
    @Override
    public void onRecordUpdate(int duration) {
        String desc = String.format("已录制%d秒", duration);
        tv_audio_record.setText(desc);
    }

    // 在录音完成时触发
    @Override
    public void onRecordFinish() {
        ck_audio_record.setChecked(false);
        Toast.makeText(this, "已结束录音，音频文件路径为"+mRecordFilePath, Toast.LENGTH_LONG).show();
    }

    // 在播音进度更新时触发
    @Override
    public void onPlayUpdate(int duration) {
        String desc = String.format("已播放%d秒", duration);
        tv_audio_play.setText(desc);
    }

    // 在播音完成时触发
    @Override
    public void onPlayFinish() {
        ck_audio_play.setChecked(false);
        Toast.makeText(this, "已结束播音", Toast.LENGTH_LONG).show();
    }

}
