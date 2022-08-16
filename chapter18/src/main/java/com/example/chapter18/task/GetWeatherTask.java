package com.example.chapter18.task;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.chapter18.bean.WeatherInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetWeatherTask extends Thread {
    private static final String TAG = "GetWeatherTask";
    // 下面查询地址中的key值需要自己到高德开放平台申请带Web服务的测试应用
    private String mQueryUrl = "https://restapi.amap.com/v3/weather/weatherInfo?city=%s&key=8df51cca4080f563eac98e1ba51bdf90";
    private Activity mAct; // 声明一个活动实例
    private OnWeatherListener mListener; // 声明一个获取天气信息的监听器对象
    private String mCityCode; // 城市代码

    public GetWeatherTask(Activity act, String city_code, OnWeatherListener listener) {
        mAct = act;
        mListener = listener;
        mCityCode = city_code;
    }

    @Override
    public void run() {
        String url = String.format(mQueryUrl, mCityCode);
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
                        "查询天气信息出错："+e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException { // 请求成功
                String resp = response.body().string();
                Log.d(TAG, "resp="+resp);
                // 下面从json串中逐级解析获得天气、温度、风向、风力、湿度
                try {
                    JSONObject obj = new JSONObject(resp);
                    JSONArray lives = obj.getJSONArray("lives");
                    JSONObject item = lives.getJSONObject(0);
                    String weather = item.getString("weather"); // 天气
                    String temperature = item.getString("temperature"); // 温度
                    String winddirection = item.getString("winddirection"); // 风向
                    String windpower = item.getString("windpower"); // 风力
                    String humidity = item.getString("humidity"); // 湿度
                    WeatherInfo weatherInfo = new WeatherInfo(weather, temperature, winddirection, windpower, humidity);
                    // 回到主线程操纵界面
                    mAct.runOnUiThread(() -> mListener.onGetWeather(weatherInfo));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 定义一个查询天气信息的监听器接口
    public interface OnWeatherListener {
        void onGetWeather(WeatherInfo weatherInfo);
    }

}
