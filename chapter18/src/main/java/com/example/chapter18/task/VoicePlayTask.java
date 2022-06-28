package com.example.chapter18.task;

import android.app.Activity;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class VoicePlayTask extends Thread {
    private final static String TAG = "VoicePlayTask";
    private Activity mAct; // 声明一个活动实例
    private File mPlayFile; // 音频文件的保存路径
    private int mFrequence; // 音频的采样频率，单位赫兹
    private int mChannel; // 音频的声道类型
    private int mFormat; // 音频的编码格式
    private boolean isCancel = false; // 是否取消播音

    public VoicePlayTask(Activity act, String filePath, int[] params) {
        mAct = act;
        mPlayFile = new File(filePath);
        mFrequence = params[0];
        mChannel = params[1];
        mFormat = params[2];
    }

    @Override
    public void run() {
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
    }

    // 取消播放实时音频
    public void cancel() {
        isCancel = true;
    }

}
