package com.example.chapter18.task;

import android.app.Activity;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.os.Handler;
import android.os.Looper;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class AudioPlayTask extends Thread {
    private final static String TAG = "AudioPlayTask";
    private Activity mAct; // 声明一个活动实例
    private OnPlayListener mListener; // 声明一个播放事件的监听器对象
    private File mPlayFile; // 音频文件的保存路径
    private int mFrequence; // 音频的采样频率，单位赫兹
    private int mChannel; // 音频的声道类型
    private int mFormat; // 音频的编码格式
    private boolean isCancel = false; // 是否取消播音
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private int mPlayTime = 0; // 已播放时间

    public AudioPlayTask(Activity act, String filePath, int[] params, OnPlayListener listener) {
        mAct = act;
        mListener = listener;
        mPlayFile = new File(filePath);
        mFrequence = params[0];
        mChannel = params[1];
        mFormat = params[2];
    }

    @Override
    public void run() {
        // 延迟1秒后启动刷新播放进度的任务
        mHandler.postDelayed(mPlayRun, 1000);
        // 定义输入流，将音频写入到AudioTrack类中，实现播放
        try (FileInputStream fis = new FileInputStream(mPlayFile);
             DataInputStream dis = new DataInputStream(fis)) {
            // 根据定义好的几个配置，来获取合适的缓冲大小
            int bufferSize = AudioTrack.getMinBufferSize(mFrequence, mChannel, mFormat);
            byte[] buffer = new byte[bufferSize]; // 创建缓冲区
            // 根据音频配置和缓冲区构建原始音频播放实例
            AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
                    mFrequence, mChannel, mFormat, bufferSize, AudioTrack.MODE_STREAM);
            // 设置需要通知的时间周期为1秒
            track.setPositionNotificationPeriod(1000);
            // 设置播放位置变化的监听器
            track.setPlaybackPositionUpdateListener(new PlaybackUpdateListener());
            track.play(); // 开始播放原始音频
            // 由于AudioTrack播放的是字节流，所以，我们需要一边播放一边读取
            while (!isCancel && dis.available() > 0) {
                int i = 0;
                // 把输入流中的数据循环读取到缓冲区
                while (dis.available() > 0 && i < buffer.length) {
                    buffer[i] = dis.readByte();
                    i++;
                }
                // 然后将数据写入到原始音频AudioTrack中
                track.write(buffer, 0, buffer.length);
            }
            track.stop(); // 取消播放任务，或者读完了，都停止原始音频播放
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mListener != null) {
            mAct.runOnUiThread(() -> mListener.onPlayFinish());
        }
        mHandler.removeCallbacks(mPlayRun); // 移除刷新播放进度的任务
    }

    // 取消播音
    public void cancel() {
        isCancel = true;
    }

    // 定义一个刷新播放进度的任务
    private Runnable mPlayRun = new Runnable() {
        @Override
        public void run() {
            mPlayTime++;
            // 延迟1秒后再次启动刷新播放进度的任务
            mHandler.postDelayed(this, 1000);
        }
    };

    // 定义一个播放位置变化的监听器
    private class PlaybackUpdateListener implements OnPlaybackPositionUpdateListener {

        // 在标记到达时触发，对应setNotificationMarkerPosition方法的设置
        @Override
        public void onMarkerReached(AudioTrack track) {}

        // 在周期到达时触发，对应setPositionNotificationPeriod方法的设置
        @Override
        public void onPeriodicNotification(AudioTrack track) {
            if (mListener != null) {
                mAct.runOnUiThread(() -> mListener.onPlayUpdate(mPlayTime));
            }
        }
    }

    // 定义一个播放事件的监听器接口
    public interface OnPlayListener {
        void onPlayUpdate(int duration); // 更新播放进度
        void onPlayFinish(); // 播放完毕
    }

}
