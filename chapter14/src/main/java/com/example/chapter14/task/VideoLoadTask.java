package com.example.chapter14.task;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.chapter14.bean.QueryResponse;
import com.example.chapter14.bean.VideoInfo;
import com.example.chapter14.constant.UrlConstant;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VideoLoadTask extends Thread {
    private static final String TAG = "VideoLoadTask";
    private Activity mAct; // 声明一个活动实例
    private VideoLoadListener mListener; // 声明一个视频加载的监听器对象

    public VideoLoadTask(Activity act, VideoLoadListener listener) {
        mAct = act;
        mListener = listener;
    }

    @Override
    public void run() {
        Log.d(TAG, "run");
        OkHttpClient client = new OkHttpClient(); // 创建一个okhttp客户端对象
        // 创建一个GET方式的请求结构
        Request request = new Request.Builder().url(UrlConstant.HTTP_PREFIX+"queryVideo").build();
        Call call = client.newCall(request); // 根据请求结构创建调用对象
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { // 请求失败
                mAct.runOnUiThread(() -> Toast.makeText(mAct,
                        "获取视频列表出错："+e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException { // 请求成功
                String resp = response.body().string();
                Log.d(TAG, "resp="+resp);
                QueryResponse queryResponse = new Gson().fromJson(resp, QueryResponse.class);
                mAct.runOnUiThread(() -> {
                    if ("0".equals(queryResponse.getCode())) {
                        mListener.onVideoLoad(queryResponse.getVideoList());
                    } else {
                        Toast.makeText(mAct, "获取视频列表失败："+queryResponse.getDesc(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // 定义一个视频加载的监听器接口，在获得响应之后回调onVideoLoad方法
    public interface VideoLoadListener {
        void onVideoLoad(List<VideoInfo> videoList);
    }

}
