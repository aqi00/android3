package com.example.chapter13;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter13.util.DateUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkhttpDownloadActivity extends AppCompatActivity {
    private final static String TAG = "OkhttpDownloadActivity";
    private final static String URL_IMAGE = "https://img-blog.csdnimg.cn/2018112123554364.png";
    private final static String URL_MP4 = "https://ptgl.fujian.gov.cn:8088/masvod/public/2021/03/19/20210319_178498bcae9_r38.mp4";
    private TextView tv_result; // 声明一个文本视图对象
    private TextView tv_progress; // 声明一个文本视图对象
    private ImageView iv_result; // 声明一个图像视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp_download);
        tv_result = findViewById(R.id.tv_result);
        tv_progress = findViewById(R.id.tv_progress);
        iv_result = findViewById(R.id.iv_result);
        findViewById(R.id.btn_download_image).setOnClickListener(v -> downloadImage());
        findViewById(R.id.btn_download_file).setOnClickListener(v -> downloadFile());
    }

    // 下载网络图片
    private void downloadImage() {
        tv_progress.setVisibility(View.GONE);
        iv_result.setVisibility(View.VISIBLE);
        OkHttpClient client = new OkHttpClient(); // 创建一个okhttp客户端对象
        // 创建一个GET方式的请求结构
        Request request = new Request.Builder().url(URL_IMAGE).build();
        Call call = client.newCall(request); // 根据请求结构创建调用对象
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { // 请求失败
                // 回到主线程操纵界面
                runOnUiThread(() -> tv_result.setText("下载网络图片报错："+e.getMessage()));
            }

            @Override
            public void onResponse(Call call, final Response response) { // 请求成功
                InputStream is = response.body().byteStream();
                // 从返回的输入流中解码获得位图数据
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                String mediaType = response.body().contentType().toString();
                long length = response.body().contentLength();
                String desc = String.format("文件类型为%s，文件大小为%d", mediaType, length);
                // 回到主线程操纵界面
                runOnUiThread(() -> {
                    tv_result.setText("下载网络图片返回："+desc);
                    iv_result.setImageBitmap(bitmap);
                });
            }
        });
    }

    // 下载网络文件
    private void downloadFile() {
        tv_progress.setVisibility(View.VISIBLE);
        iv_result.setVisibility(View.GONE);
        OkHttpClient client = new OkHttpClient(); // 创建一个okhttp客户端对象
        // 创建一个GET方式的请求结构
        Request request = new Request.Builder().url(URL_MP4).build();
        Call call = client.newCall(request); // 根据请求结构创建调用对象
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { // 请求失败
                // 回到主线程操纵界面
                runOnUiThread(() -> tv_result.setText("下载网络文件报错："+e.getMessage()));
            }

            @Override
            public void onResponse(Call call, final Response response) { // 请求成功
                String mediaType = response.body().contentType().toString();
                long length = response.body().contentLength();
                String desc = String.format("文件类型为%s，文件大小为%d", mediaType, length);
                // 回到主线程操纵界面
                runOnUiThread(() -> tv_result.setText("下载网络文件返回："+desc));
                String path = String.format("%s/%s.mp4",
                        getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),
                        DateUtil.getNowDateTime());
                // 下面从返回的输入流中读取字节数据并保存为本地文件
                try (InputStream is = response.body().byteStream();
                     FileOutputStream fos = new FileOutputStream(path)) {
                    byte[] buf = new byte[100 * 1024];
                    int sum=0, len=0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / length * 100);
                        String detail = String.format("文件保存在%s。已下载%d%%", path, progress);
                        // 回到主线程操纵界面
                        runOnUiThread(() -> tv_progress.setText(detail));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}