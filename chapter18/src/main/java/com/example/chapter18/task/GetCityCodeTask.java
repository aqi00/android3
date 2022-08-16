package com.example.chapter18.task;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.chapter18.bean.CityInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetCityCodeTask extends Thread {
    private static final String TAG = "GetCityCodeTask";
    // 下面查询地址中的key值需要自己到高德开放平台申请带Web服务的测试应用
    private String mQueryUrl = "https://restapi.amap.com/v3/config/district?keywords=%s&subdistrict=1&key=8df51cca4080f563eac98e1ba51bdf90";
    private Activity mAct; // 声明一个活动实例
    private OnCityCodeListener mListener; // 声明一个获取城市编码的监听器对象
    private CityInfo mCityInfo; // 城市名称

    public GetCityCodeTask(Activity act, CityInfo cityInfo, OnCityCodeListener listener) {
        mAct = act;
        mListener = listener;
        mCityInfo = cityInfo;
    }

    @Override
    public void run() {
        String url = String.format(mQueryUrl, mCityInfo.city_name);
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
                        "查询城市编码出错："+e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException { // 请求成功
                String resp = response.body().string();
                Log.d(TAG, "resp="+resp);
                // 下面从json串中逐级解析adcode字段获得城市代码
                try {
                    JSONObject obj = new JSONObject(resp);
                    JSONArray districts = obj.getJSONArray("districts");
                    JSONObject sub = districts.getJSONObject(0);
                    String adcode = sub.getString("adcode");
                    JSONArray sub_districts = sub.getJSONArray("districts");
                    for (int i=0; i<sub_districts.length(); i++) {
                        JSONObject item = sub_districts.getJSONObject(i);
                        // 存在该区县则取区县的区域代码，否则取该城市的区域代码
                        if (mCityInfo.county_name.equals(item.getString("name"))) {
                            adcode = item.getString("adcode");
                            break;
                        }
                    }
                    String city_code = adcode;
                    // 回到主线程操纵界面
                    mAct.runOnUiThread(() -> mListener.onGetCityCode(city_code));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 定义一个查询城市编码的监听器接口
    public interface OnCityCodeListener {
        void onGetCityCode(String city_code);
    }

}
