package com.example.chapter18.task;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.AudioRecord.OnRecordPositionUpdateListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class AudioRecordTask extends Thread {
    private final static String TAG = "AudioRecordTask";
    private Activity mAct; // 声明一个活动实例
    private OnRecordListener mListener; // 声明一个录制事件的监听器对象
    private File mRecordFile; // 音频文件的保存路径
    private int mFrequence; // 音频的采样频率，单位赫兹
    private int mChannel; // 音频的声道类型
    private int mFormat; // 音频的编码格式
    private boolean isCancel = false; // 是否取消录音
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private int mRecordTime = 0; // 已录制时间

    public AudioRecordTask(Activity act, String filePath, int[] params, OnRecordListener listener) {
        mAct = act;
        mListener = listener;
        mRecordFile = new File(filePath);
        mFrequence = params[0];
        mChannel = params[1];
        mFormat = params[2];
    }

    @Override
    public void run() {
        // 延迟1秒后启动刷新录制进度的任务
        mHandler.postDelayed(mRecordRun, 1000);
        // 开通输出流到指定的文件
        try (FileOutputStream fos = new FileOutputStream(mRecordFile);
             DataOutputStream dos = new DataOutputStream(fos)) {
            // 根据定义好的几个配置，来获取合适的缓冲大小
            int bufferSize = AudioRecord.getMinBufferSize(mFrequence, mChannel, mFormat);
            byte[] buffer = new byte[bufferSize]; // 创建缓冲区
            if (ActivityCompat.checkSelfPermission(mAct, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            // 根据音频配置和缓冲区构建原始音频录制实例
            AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    mFrequence, mChannel, mFormat, bufferSize);
            // 设置需要通知的时间周期为1秒
            record.setPositionNotificationPeriod(1000);
            // 设置录制位置变化的监听器
            record.setRecordPositionUpdateListener(new RecordUpdateListener());
            record.startRecording(); // 开始录制原始音频
            // 没有取消录制，则持续读取缓冲区
            while (!isCancel) {
                int readSize = record.read(buffer, 0, buffer.length);
                // 循环将缓冲区中的音频数据写入到输出流
                for (int i = 0; i < readSize; i++) {
                    dos.writeByte(buffer[i]);
                }
            }
            record.stop(); // 停止原始音频录制
            Log.d(TAG, "file_path=" + mRecordFile.getAbsolutePath() + ", length=" + mRecordFile.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mListener != null) {
            mAct.runOnUiThread(() -> mListener.onRecordFinish());
        }
        mHandler.removeCallbacks(mRecordRun); // 移除刷新录制进度的任务
    }

    // 取消录音
    public void cancel() {
        isCancel = true;
    }

    // 定义一个刷新录制进度的任务
    private Runnable mRecordRun = new Runnable() {
        @Override
        public void run() {
            mRecordTime++;
            // 延迟1秒后再次启动刷新录制进度的任务
            mHandler.postDelayed(this, 1000);
        }
    };

    // 定义一个录制位置变化的监听器
    private class RecordUpdateListener implements OnRecordPositionUpdateListener {

        // 在标记到达时触发，对应setNotificationMarkerPosition方法的设置
        @Override
        public void onMarkerReached(AudioRecord recorder) {}

        // 在周期到达时触发，对应setPositionNotificationPeriod方法的设置
        @Override
        public void onPeriodicNotification(AudioRecord recorder) {
            if (mListener != null) {
                mAct.runOnUiThread(() -> mListener.onRecordUpdate(mRecordTime));
            }
        }
    }

    // 定义一个录制事件的监听器接口
    public interface OnRecordListener {
        void onRecordUpdate(int duration); // 更新录制进度
        void onRecordFinish(); // 录制完毕
    }

}
