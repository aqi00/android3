package com.example.chapter19.task;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetAddressTask extends Thread {
    private static final String TAG = "GetAddressTask";
    private String mQueryUrl = "https://api.tianditu.gov.cn/geocoder?postStr={'lon':%f,'lat':%f,'ver':1}&type=geocode&tk=253b3bd69713d4bdfdc116255f379841";
    private Activity mAct; // 声明一个活动实例
    private OnAddressListener mListener; // 声明一个获取地址的监听器对象
    private Location mLocation; // 声明一个定位对象

    public GetAddressTask(Activity act, Location location, OnAddressListener listener) {
        mAct = act;
        mListener = listener;
        mLocation = location;
    }

    @Override
    public void run() {
        String url = String.format(mQueryUrl, mLocation.getLongitude(), mLocation.getLatitude());
        Log.d(TAG, "url="+url);
        OkHttpClient client = new OkHttpClient(); // 创建一个okhttp客户端对象
        // 创建一个GET方式的请求结构
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request); // 根据请求结构创建调用对象
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { // 请求失败
                // 回到主线程操纵界面
                mAct.runOnUiThread(() -> Toast.makeText(mAct,
                        "查询详细地址出错："+e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException { // 请求成功
                String resp = response.body().string();
                Log.d(TAG, "resp="+resp);
                // 下面从json串中逐级解析formatted_address字段获得详细地址描述
                try {
                    JSONObject obj = new JSONObject(resp);
                    JSONObject result = obj.getJSONObject("result");
                    String address = result.getString("formatted_address");
                    // 回到主线程操纵界面
                    mAct.runOnUiThread(() -> mListener.onFindAddress(address));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 定义一个查询详细地址的监听器接口
    public interface OnAddressListener {
        void onFindAddress(String address);
    }

}
