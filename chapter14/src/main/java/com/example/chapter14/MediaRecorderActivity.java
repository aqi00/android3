package com.example.chapter14;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter14.util.MediaUtil;

import java.util.Timer;
import java.util.TimerTask;

public class MediaRecorderActivity extends AppCompatActivity implements MediaRecorder.OnInfoListener {
    private static final String TAG = "MediaRecorderActivity";
    private Button btn_record;
    private LinearLayout ll_progress;
    private ProgressBar pb_record; // 声明一个进度条对象
    private TextView tv_progress;
    private ImageView iv_audio; // 该图标充当播放按钮
    private MediaRecorder mMediaRecorder = new MediaRecorder(); // 媒体录制器
    private boolean isRecording = false; // 是否正在录制
    private int mAudioEncoder; // 音频编码
    private int mOutputFormat; // 输出格式
    private int mDuration; // 录制时长
    private String mRecordFilePath; // 录制文件的保存路径
    private Timer mTimer = new Timer(); // 计时器
    private int mTimeCount; // 时间计数
    private MediaPlayer mMediaPlayer = new MediaPlayer(); // 媒体播放器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recorder);
        btn_record = findViewById(R.id.btn_record);
        ll_progress = findViewById(R.id.ll_progress);
        pb_record = findViewById(R.id.pb_record);
        tv_progress = findViewById(R.id.tv_progress);
        iv_audio = findViewById(R.id.iv_audio);
        btn_record.setOnClickListener(v -> {
            if (!isRecording) { // 未在录音
                startRecord(); // 开始录音
            } else { // 正在录音
                stopRecord(); // 停止录音
            }
        });
        iv_audio.setOnClickListener(v -> playAudio());
        initEncoderSpinner(); // 初始化音频编码的下拉框
        initFormatSpinner(); // 初始化输出格式的下拉框
        initDurationSpinner(); // 初始化录制时长的下拉框
    }

    // 初始化音频编码的下拉框
    private void initEncoderSpinner() {
        ArrayAdapter<String> encoderAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, encoderDescArray);
        Spinner sp_encoder = findViewById(R.id.sp_encoder);
        sp_encoder.setPrompt("请选择音频编码"); // 设置下拉框的标题
        sp_encoder.setAdapter(encoderAdapter); // 设置下拉框的数组适配器
        // 给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sp_encoder.setOnItemSelectedListener(new EncoderSelectedListener());
        sp_encoder.setSelection(0); // 设置下拉框默认显示第一项
    }

    private String[] encoderDescArray = {
            "默认编码",
            "窄带编码",
            "宽带编码",
            "低复杂度的高级编码",
            "高效率的高级编码",
            "增强型低延时的高级编码"
    };
    private int[] encoderArray = {
            MediaRecorder.AudioEncoder.DEFAULT,
            MediaRecorder.AudioEncoder.AMR_NB,
            MediaRecorder.AudioEncoder.AMR_WB,
            MediaRecorder.AudioEncoder.AAC,
            MediaRecorder.AudioEncoder.HE_AAC,
            MediaRecorder.AudioEncoder.AAC_ELD
    };

    class EncoderSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mAudioEncoder = encoderArray[arg2];
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 初始化输出格式的下拉框
    private void initFormatSpinner() {
        ArrayAdapter<String> formatAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, formatDescArray);
        Spinner sp_format = findViewById(R.id.sp_format);
        sp_format.setPrompt("请选择输出格式"); // 设置下拉框的标题
        sp_format.setAdapter(formatAdapter); // 设置下拉框的数组适配器
        sp_format.setSelection(0); // 设置下拉框默认显示第一项
        // 给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sp_format.setOnItemSelectedListener(new FormatSelectedListener());
    }

    private String[] formatDescArray = {
            "默认格式",
            "窄带格式",
            "宽带格式",
            "高级的音频传输流格式"
    };
    private int[] formatArray = {
            MediaRecorder.OutputFormat.DEFAULT,
            MediaRecorder.OutputFormat.AMR_NB,
            MediaRecorder.OutputFormat.AMR_WB,
            MediaRecorder.OutputFormat.AAC_ADTS
    };
    class FormatSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mOutputFormat = formatArray[arg2];
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 初始化录制时长的下拉框
    private void initDurationSpinner() {
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, durationDescArray);
        Spinner sp_duration = findViewById(R.id.sp_duration);
        sp_duration.setPrompt("请选择录制时长"); // 设置下拉框的标题
        sp_duration.setAdapter(durationAdapter); // 设置下拉框的数组适配器
        sp_duration.setSelection(0); // 设置下拉框默认显示第一项
        // 给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sp_duration.setOnItemSelectedListener(new DurationSelectedListener());
    }

    private String[] durationDescArray = {"5秒", "10秒", "20秒", "30秒", "60秒"};
    private int[] durationArray = {5, 10, 20, 30, 60};
    class DurationSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mDuration = durationArray[arg2];
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    private void playAudio() {
        mMediaPlayer.reset(); // 重置媒体播放器
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // 设置音频流的类型为音乐
        try {
            mMediaPlayer.setDataSource(mRecordFilePath); // 设置媒体数据的文件路径
            mMediaPlayer.prepare(); // 媒体播放器准备就绪
            mMediaPlayer.start(); // 媒体播放器开始播放
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 开始录音
    private void startRecord() {
        Log.d(TAG, "startRecord mAudioEncoder="+mAudioEncoder+", mOutputFormat="+mOutputFormat+", mDuration="+mDuration);
        ll_progress.setVisibility(View.VISIBLE);
        isRecording = !isRecording;
        btn_record.setText("停止录制");
        pb_record.setMax(mDuration); // 设置进度条的最大值
        mTimeCount = 0; // 时间计数清零
        mTimer = new Timer(); // 创建一个计时器
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                pb_record.setProgress(mTimeCount); // 设置进度条的当前进度
                tv_progress.setText(MediaUtil.formatDuration(mTimeCount*1000));
                mTimeCount++;
            }
        }, 0, 1000); // 计时器每隔一秒就更新进度条上的录制进度
        // 获取本次录制的媒体文件路径
        mRecordFilePath = MediaUtil.getRecordFilePath(this, "RecordAudio", ".amr");
        // 下面是媒体录制器的处理代码
        mMediaRecorder.reset(); // 重置媒体录制器
        mMediaRecorder.setOnInfoListener(this); // 设置媒体录制器的信息监听器
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); // 设置音频源为麦克风
        mMediaRecorder.setOutputFormat(mOutputFormat); // 设置媒体的输出格式。该方法要先于setAudioEncoder调用
        mMediaRecorder.setAudioEncoder(mAudioEncoder); // 设置媒体的音频编码器
        // mMediaRecorder.setAudioSamplingRate(8); // 设置媒体的音频采样率。可选
        // mMediaRecorder.setAudioChannels(2); // 设置媒体的音频声道数。可选
        // mMediaRecorder.setAudioEncodingBitRate(1024); // 设置音频每秒录制的字节数。可选
        mMediaRecorder.setMaxDuration(mDuration * 1000); // 设置媒体的最大录制时长
        // mMediaRecorder.setMaxFileSize(1024*1024*10); // 设置媒体的最大文件大小
        // setMaxFileSize与setMaxDuration设置其一即可
        mMediaRecorder.setOutputFile(mRecordFilePath); // 设置媒体文件的保存路径
        try {
            mMediaRecorder.prepare(); // 媒体录制器准备就绪
            mMediaRecorder.start(); // 媒体录制器开始录制
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 停止录音
    private void stopRecord() {
        isRecording = !isRecording;
        btn_record.setText("开始录制");
        mTimer.cancel(); // 取消定时器
        mMediaRecorder.stop(); // 媒体录制器停止录制
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        // 录制达到最大时长，或者达到文件大小限制，都停止录制
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED
                || what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
            stopRecord(); // 停止录音
            iv_audio.setVisibility(View.VISIBLE);
            Toast.makeText(this, "已结束录制，音频文件路径为"+mRecordFilePath, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!TextUtils.isEmpty(mRecordFilePath) && isRecording) {
            stopRecord(); // 停止录音
        }
        if (mMediaPlayer.isPlaying()) { // 如果正在播放
            mMediaPlayer.stop(); // 停止播放
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaRecorder.release(); // 释放媒体录制器
        mMediaPlayer.release(); // 释放媒体播放器
    }
}
