package com.example.chapter20.webrtc;

import org.webrtc.Logging;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;

public class ProxyVideoSink implements VideoSink {
    private static final String TAG = "ProxyVideoSink";
    private VideoSink target; // 声明一个目标的视频管道

    @Override
    synchronized public void onFrame(VideoFrame frame) {
        if (target == null) {
            Logging.d(TAG, "Dropping frame in proxy because target is null.");
            return;
        }
        target.onFrame(frame);
    }

    // 设置视频轨道中对方的渲染图层
    synchronized public void setTarget(VideoSink target) {
        this.target = target;
    }

}