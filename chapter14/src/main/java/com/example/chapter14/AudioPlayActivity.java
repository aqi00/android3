package com.example.chapter14;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.example.chapter14.adapter.AudioRecyclerAdapter;
import com.example.chapter14.bean.AudioInfo;
import com.example.chapter14.util.FileUtil;
import com.example.chapter14.widget.RecyclerExtras;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AudioPlayActivity extends AppCompatActivity implements RecyclerExtras.OnItemClickListener {
    private final static String TAG = "AudioPlayActivity";
    private RecyclerView rv_audio; // 音频列表的循环视图
    private List<AudioInfo> mAudioList = new ArrayList<AudioInfo>(); // 音频列表
    private Uri mAudioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; // 音频库的Uri
    private String[] mAudioColumn = new String[]{ // 媒体库的字段名称数组
            MediaStore.Audio.Media._ID, // 编号
            MediaStore.Audio.Media.TITLE, // 标题
            MediaStore.Audio.Media.DURATION, // 播放时长
            MediaStore.Audio.Media.SIZE, // 文件大小
            MediaStore.Audio.Media.DATA}; // 文件路径
    private AudioRecyclerAdapter mAdapter; // 音频列表的适配器
    private MediaPlayer mMediaPlayer = new MediaPlayer(); // 媒体播放器
    private Timer mTimer = new Timer(); // 计时器
    private int mLastPosition = -1; // 上次播放的音频序号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_play);
        rv_audio = findViewById(R.id.rv_audio);
        loadAudioList(); // 加载音频列表
        showAudioList(); // 显示音频列表
    }

    // 加载音频列表
    private void loadAudioList() {
        mAudioList.clear(); // 清空音频列表
        // 通过内容解析器查询音频库，并返回结果集的游标。记录结果按照修改时间降序返回
        Cursor cursor = getContentResolver().query(mAudioUri, mAudioColumn,
                null, null, "date_modified desc");
        if (cursor != null) {
            Log.d(TAG, "cursor is not null");
            // 下面遍历结果集，并逐个添加到音频列表。简单起见只挑选前十个音频
            for (int i=0; i<10 && cursor.moveToNext(); i++) {
                Log.d(TAG, "cursor i="+i);
                AudioInfo audio = new AudioInfo(); // 创建一个音频信息对象
                audio.setId(cursor.getLong(0)); // 设置音频编号
                audio.setTitle(cursor.getString(1)); // 设置音频标题
                audio.setDuration(cursor.getInt(2)); // 设置音频时长
                audio.setSize(cursor.getLong(3)); // 设置音频大小
                audio.setPath(cursor.getString(4)); // 设置音频路径
                Log.d(TAG, audio.getTitle() + " " + audio.getDuration() + " " + audio.getSize() + " " + audio.getPath());
                if (!FileUtil.checkFileUri(this, audio.getPath())) {
                    i--;
                    continue;
                }
                mAudioList.add(audio); // 添加至音频列表
            }
            cursor.close(); // 关闭数据库游标
        } else {
            Log.d(TAG, "cursor is null");
        }
    }

    // 显示音频列表
    private void showAudioList() {
        // 创建一个水平方向的线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_audio.setLayoutManager(manager); // 设置循环视图的布局管理器
        mAdapter = new AudioRecyclerAdapter(this, mAudioList); // 创建音频列表的线性适配器
        mAdapter.setOnItemClickListener(this); // 设置线性列表的点击监听器
        rv_audio.setAdapter(mAdapter); // 设置循环视图的列表适配器
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel(); // 取消计时器
        if (mMediaPlayer.isPlaying()) { // 是否正在播放
            mMediaPlayer.stop(); // 结束播放
        }
        mMediaPlayer.release(); // 释放媒体播放器
    }

    @Override
    public void onItemClick(View view, final int position) {
        if (mLastPosition!=-1 && mLastPosition!=position) {
            AudioInfo last_audio = mAudioList.get(mLastPosition);
            last_audio.setProgress(-1); // 当前进度设为-1表示没在播放
            mAudioList.set(mLastPosition, last_audio);
            mAdapter.notifyItemChanged(mLastPosition); // 刷新此处的列表项
        }
        mLastPosition = position;
        final AudioInfo audio = mAudioList.get(position);
        Log.d(TAG, "onItemClick position="+position+",audio.getPath()="+audio.getPath());
        mTimer.cancel(); // 取消计时器
        mMediaPlayer.reset(); // 重置媒体播放器
        // mMediaPlayer.setVolume(0.5f, 0.5f); // 设置音量，可选
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // 设置音频流的类型为音乐
        try {
            mMediaPlayer.setDataSource(audio.getPath()); // 设置媒体数据的文件路径
            mMediaPlayer.prepare(); // 媒体播放器准备就绪
            mMediaPlayer.start(); // 媒体播放器开始播放
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTimer = new Timer(); // 创建一个计时器
        mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    audio.setProgress(mMediaPlayer.getCurrentPosition()); // 设置进度条的当前进度
                    mAudioList.set(position, audio);
                    // 界面刷新操作需要在主线程执行，故而向处理器发送消息，由处理器在主线程更新界面
                    mHandler.sendEmptyMessage(position);
                    Log.d(TAG, "CurrentPosition="+mMediaPlayer.getCurrentPosition()+",position="+position);
                }
                }, 0, 1000); // 计时器每隔一秒就更新进度条上的播放进度
    }

    private Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAdapter.notifyItemChanged(msg.what); // 刷新此处的列表项
        }
    };

}
