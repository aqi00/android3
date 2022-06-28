package com.example.chapter15.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.chapter15.bean.ParseResponse;
import com.example.chapter15.entity.BookInfo;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OfficeUtil {
    private static final String TAG = "OfficeUtil";

    // 向服务器上传文档，异步返回文档解析结果
    public static void uploadDocument(Activity act, String srcPath, String url, UploadListener listener) {
        Log.d(TAG, "srcPath="+srcPath);
        Log.d(TAG, "url="+url);
        File file = new File(srcPath); // 根据文件路径创建文件对象
        // 创建分段内容的建造器对象
        MultipartBody.Builder builder = new MultipartBody.Builder();
        // 往建造器对象添加文本格式的分段数据
        builder.addFormDataPart("fileName", file.getName());
        // 往建造器对象添加图像格式的分段数据
        builder.addFormDataPart("document", file.getName(),
                RequestBody.create(file, MediaType.parse("application/*")));
        RequestBody body = builder.build(); // 根据建造器生成请求结构
        OkHttpClient client = new OkHttpClient(); // 创建一个okhttp客户端对象
        // 创建一个POST方式的请求结构
        Request request = new Request.Builder().post(body).url(url).build();
        Call call = client.newCall(request); // 根据请求结构创建调用对象
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { // 请求失败
                // 回到主线程操纵界面
                act.runOnUiThread(() -> listener.onFail(e));
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException { // 请求成功
                String resp = response.body().string();
                Log.d(TAG, "resp="+resp);
                ParseResponse parseResponse = new Gson().fromJson(resp, ParseResponse.class);
                // 回到主线程操纵界面
                act.runOnUiThread(() -> listener.onSucc(parseResponse));
            }
        });
    }

    public static void downloadImage(Activity act, String url, String imagePath, DownloadListener listener) {
        OkHttpClient client = new OkHttpClient(); // 创建一个okhttp客户端对象
        // 创建一个GET方式的请求结构
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request); // 根据请求结构创建调用对象
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { // 请求失败
                // 回到主线程操纵界面
                act.runOnUiThread(() -> listener.onFail(e));
            }

            @Override
            public void onResponse(Call call, final Response response) { // 请求成功
                InputStream is = response.body().byteStream();
                // 从返回的输入流中解码获得位图数据
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                Log.d(TAG, "imagePath="+imagePath);
                BitmapUtil.saveImage(imagePath, bitmap);
                // 回到主线程操纵界面
                act.runOnUiThread(() -> listener.onSucc(imagePath));
            }
        });
    }

    public interface UploadListener {
        public void onFail(IOException e);
        public void onSucc(ParseResponse parseResponse);
    }

    public interface DownloadListener {
        public void onFail(IOException e);
        public void onSucc(String imagePath);
    }
}
