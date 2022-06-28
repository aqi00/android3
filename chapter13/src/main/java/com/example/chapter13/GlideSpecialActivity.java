package com.example.chapter13;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.VideoDecoder;
import com.bumptech.glide.request.RequestOptions;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@SuppressLint("CheckResult")
public class GlideSpecialActivity extends AppCompatActivity {
    private final static String TAG = "GlideSpecialActivity";
    private ImageView iv_cover;
    private final static String URL_MP4 = "https://ptgl.fujian.gov.cn:8088/masvod/public/2021/03/19/20210319_178498bcae9_r38.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide_special);
        iv_cover = findViewById(R.id.iv_cover);
        findViewById(R.id.btn_gif).setOnClickListener(v -> {
            Glide.with(this).load(R.drawable.happy).into(iv_cover);
        });
        // 注册一个善后工作的活动结果启动器，获取指定类型的内容
        ActivityResultLauncher launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) { // 视频路径非空，则加载视频封面
                Glide.with(this).load(uri).into(iv_cover);
            }
        });
        findViewById(R.id.btn_local_cover).setOnClickListener(v -> launcher.launch("video/*"));
        // 加载第10秒处的视频画面
        findViewById(R.id.btn_network_one).setOnClickListener(v -> {
            // 获取指定时间点的请求参数
            RequestOptions options = getOptions(10);
            // 加载网络视频的封面图片
            Glide.with(this).load(URL_MP4).apply(options).into(iv_cover);
        });
        // 加载第45秒处的视频画面
        findViewById(R.id.btn_network_nine).setOnClickListener(v -> {
            // 获取指定时间点的请求参数
            RequestOptions options = getOptions(45);
            // 加载网络视频的封面图片
            Glide.with(this).load(URL_MP4).apply(options).into(iv_cover);
        });
    }

    // 获取指定时间点的请求参数
    private RequestOptions getOptions(int position) {
        // 指定某个时间位置的帧，单位微秒
        RequestOptions options = RequestOptions.frameOf(position*1000*1000);
        // 获取最近的视频帧
        options.set(VideoDecoder.FRAME_OPTION, MediaMetadataRetriever.OPTION_CLOSEST);
        // 执行从视频帧到位图对象的转换操作
        options.transform(new BitmapTransformation() {
            @Override
            protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
                return toTransform;
            }

            @Override
            public void updateDiskCacheKey(MessageDigest messageDigest) {
                try {
                    messageDigest.update((getPackageName()).getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return options;
    }
}