package com.example.chapter18.task;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class VoiceRecognizeTask extends Thread {
    private final static String TAG = "VoiceRecognizeTask";
    private Activity mAct; // 声明一个活动实例
    private int mFrequence = 16000; // 音频的采样频率，单位赫兹
    private int mChannel = AudioFormat.CHANNEL_IN_MONO; // 音频的声道类型
    private int mFormat = AudioFormat.ENCODING_PCM_16BIT; // 音频的编码格式
    private boolean isCancel = false; // 是否取消录音
    private AsrClientEndpoint mAsrTask; // 语音识别任务

    public VoiceRecognizeTask(Activity act, AsrClientEndpoint asrTask) {
        mAct = act;
        mAsrTask = asrTask;
    }

    @Override
    public void run() {
        // 根据定义好的几个配置，来获取合适的缓冲大小
        int bufferSize = AudioRecord.getMinBufferSize(mFrequence, mChannel, mFormat);
        bufferSize = Math.max(bufferSize, 9600);
        byte[] buffer = new byte[bufferSize]; // 创建缓冲区
        // 根据音频配置和缓冲区构建原始音频录制实例
        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC,
                mFrequence, mChannel, mFormat, bufferSize);
        // 设置需要通知的时间周期为1秒
        record.setPositionNotificationPeriod(1000);
        record.startRecording(); // 开始录制原始音频
        int i=0;
        // 没有取消录制，则持续读取缓冲区
        while (!isCancel) {
            int bufferReadResult = record.read(buffer, 0, buffer.length);
            mAsrTask.sendRealtimeAudio(i++, buffer, bufferReadResult);
        }
        record.stop(); // 停止原始音频录制
    }

    // 取消实时录音
    public void cancel() {
        isCancel = true;
        mAsrTask.stopAsr(); // 停止语音识别
    }

}
