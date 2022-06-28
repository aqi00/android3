package com.example.chapter08.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.example.chapter08.MainActivity;
import com.example.chapter08.R;

public class MusicService extends Service {
    private static final String TAG = "MusicService";
    private final IBinder mBinder = new LocalBinder(); // 创建一个粘合剂对象
    private String mSong; // 歌曲名称
    private boolean isPlaying = true; // 是否正在播放
    private int mProcess = 0; // 播放进度
    // 定义一个当前服务的粘合剂，用于将该服务黏到活动页面的进程中
    public class LocalBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder; // 返回该服务的粘合剂对象
    }

    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    // 定义一个音乐播放任务
    private Runnable mPlay = new Runnable() {
        @Override
        public void run() {
            if (isPlaying) {
                if (mProcess < 100) { // 尚未播放完毕
                    mProcess += 2;
                    mHandler.postDelayed(this, 1000);
                } else { // 已经播放完毕
                    mProcess = 100;
                }
            }
            sendNotify(MusicService.this, mSong, isPlaying, mProcess); // 发送前台通知
        }
    };

    // 发送前台通知
    private void sendNotify(Context ctx, String song, boolean isPlaying, int progress) {
        String message = String.format("歌曲%s", isPlaying?"正在播放":"暂停播放");
        // 创建一个跳转到活动页面的意图
        Intent intent = new Intent(ctx, MainActivity.class);
        // 创建一个用于页面跳转的延迟意图
        PendingIntent clickIntent = PendingIntent.getActivity(ctx,
                R.string.app_name, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 创建一个通知消息的建造器
        Notification.Builder builder = new Notification.Builder(ctx);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0开始必须给每个通知分配对应的渠道
            builder = new Notification.Builder(ctx, getString(R.string.app_name));
        }
        builder.setContentIntent(clickIntent) // 设置内容的点击意图
                .setSmallIcon(R.drawable.tt_s) // 设置应用名称左边的小图标
                // 设置通知栏右边的大图标
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.tt))
                .setProgress(100, progress, false) // 设置进度条与当前进度
                .setContentTitle(song) // 设置通知栏里面的标题文本
                .setContentText(message); // 设置通知栏里面的内容文本
        Notification notify = builder.build(); // 根据通知建造器构建一个通知对象
        startForeground(2, notify); // 把服务推送到前台的通知栏
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        isPlaying = intent.getBooleanExtra("is_play", true); // 获取是否正在播放
        mSong = intent.getStringExtra("song"); // 获取歌曲名称
        Log.d(TAG, "isPlaying=" + isPlaying + ", mSong=" + mSong);
        mHandler.postDelayed(mPlay, 200); // 延迟200毫秒后启动音乐播放任务
        return START_STICKY;
    }

}
