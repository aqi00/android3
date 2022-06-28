package com.example.chapter14.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.chapter14.R;
import com.example.chapter14.bean.VideoInfo;
import com.example.chapter14.constant.UrlConstant;
import com.example.chapter14.util.DialogUtil;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;

public class VideoFragment extends Fragment {
    private static final String TAG = "VideoFragment";
    protected View mView; // 声明一个视图对象
    protected Context mContext; // 声明一个上下文对象
    private VideoInfo mVideoInfo; // 视频信息
    private ExoPlayer mPlayer; // 新型播放器对象

    // 获取该碎片的一个实例
    public static VideoFragment newInstance(int position, VideoInfo videoInfo) {
        VideoFragment fragment = new VideoFragment(); // 创建该碎片的一个实例
        Bundle bundle = new Bundle(); // 创建一个新包裹
        bundle.putSerializable("video_info", videoInfo); // 往包裹存入视频信息
        fragment.setArguments(bundle); // 把包裹塞给碎片
        return fragment; // 返回碎片实例
    }

    // 创建碎片视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity(); // 获取活动页面的上下文
        if (getArguments() != null) { // 如果碎片携带有包裹，就打开包裹获取参数信息
            mVideoInfo = (VideoInfo) getArguments().getSerializable("video_info");
        }
        // 根据布局文件item_video.xml生成视图对象
        mView = inflater.inflate(R.layout.item_video, container, false);
        TextView tv_place = mView.findViewById(R.id.tv_place);
        TextView tv_desc = mView.findViewById(R.id.tv_desc);
        tv_place.setText(mVideoInfo.getDate()+"  "+mVideoInfo.getAddress());
        tv_desc.setText(mVideoInfo.getDesc());
        mView.findViewById(R.id.iv_love).setOnClickListener(v -> DialogUtil.showDialog(mContext, "您已将该视频加入收藏"));
        mView.findViewById(R.id.iv_comment).setOnClickListener(v -> DialogUtil.showDialog(mContext, "您已对该视频发表评论"));
        mView.findViewById(R.id.iv_share).setOnClickListener(v -> DialogUtil.showDialog(mContext, "您已在朋友圈分享视频"));
        StyledPlayerView pv_content = mView.findViewById(R.id.pv_content);
        mPlayer = new ExoPlayer.Builder(mContext).build();
        pv_content.setPlayer(mPlayer); // 设置播放器视图的播放器对象
        prepareVideo(UrlConstant.HTTP_PREFIX+mVideoInfo.getVideo()); // 准备在线视频
        return mView; // 返回该碎片的视图对象
    }

    // 准备在线视频
    private void prepareVideo(String videoUrl) {
        // 创建HTTP在线视频的工厂对象
        DataSource.Factory factory = new DefaultDataSource.Factory(mContext);
        // 创建指定地址的媒体对象
        MediaItem videoItem = new MediaItem.Builder().setUri(Uri.parse(videoUrl)).build();
        // 基于工厂对象和媒体对象创建媒体来源
        MediaSource videoSource = new ProgressiveMediaSource.Factory(factory).createMediaSource(videoItem);
        mPlayer.setMediaSource(videoSource); // 设置播放器的媒体来源
        // 设置播放器的重播模式，REPEAT_MODE_ALL表示反复重播
        mPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);
        mPlayer.prepare(); // 播放器准备就绪
    }

    private long mCurrentPosition = 0; // 当前的播放位置
    @Override
    public void onResume() {
        super.onResume();
        // 恢复页面时立即从上次断点开始播放视频
        if (mCurrentPosition>0 && !mPlayer.isPlaying()) {
            mPlayer.seekTo(mCurrentPosition); // 找到指定位置
        }
        mPlayer.play(); // 播放器开始播放
    }

    @Override
    public void onPause() {
        super.onPause();
        // 暂停页面时保存当前的播放进度
        if (mPlayer.isPlaying()) { // 播放器正在播放
            // 获得播放器当前的播放位置
            mCurrentPosition = mPlayer.getCurrentPosition();
            mPlayer.pause(); // 播放器暂停播放
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPlayer.release(); // 释放播放器资源
    }
}
