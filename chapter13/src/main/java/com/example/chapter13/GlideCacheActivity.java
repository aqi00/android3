package com.example.chapter13;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.File;

@SuppressLint("CheckResult")
public class GlideCacheActivity extends AppCompatActivity {
    private static final String TAG = "GlideCacheActivity";
    private ImageView iv_network;
    private String mImageUrl = "http://b68.photo.store.qq.com/psu?/0664e98e-f40a-4899-b6d6-4f4f1d94ef3c/4qBaAk.83Z9Zw9XIZUwtV**iFrAWIquow4FP0aIIQSc!/b/YWWPjCjyCgAAYra8lShRCwAA";
    private CheckBox ck_seize; // 声明一个复选框对象。是否启用占位图
    private CheckBox ck_error; // 声明一个复选框对象。是否启用出错图
    private CheckBox ck_original; // 声明一个复选框对象。是否加载原图片
    private CheckBox ck_transition; // 声明一个复选框对象。是否呈现渐变动画
    private int mCacheStrategy; // 缓存策略的类型

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide_cache);
        iv_network = findViewById(R.id.iv_network);
        ck_seize = findViewById(R.id.ck_seize);
        ck_error = findViewById(R.id.ck_error);
        ck_original = findViewById(R.id.ck_original);
        ck_transition = findViewById(R.id.ck_transition);
        ck_seize.setOnCheckedChangeListener((buttonView, isChecked) -> showNetworkImage());
        ck_error.setOnCheckedChangeListener((buttonView, isChecked) -> showNetworkImage());
        ck_original.setOnCheckedChangeListener((buttonView, isChecked) -> showNetworkImage());
        ck_transition.setOnCheckedChangeListener((buttonView, isChecked) -> showNetworkImage());
        initStrategySpinner(); // 初始化缓存策略的下拉框
        findViewById(R.id.btn_download).setOnClickListener(v -> downloadImage(mImageUrl));
    }

    // 初始化缓存策略的下拉框
    private void initStrategySpinner() {
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, strategyArray);
        Spinner sp_cache_strategy = findViewById(R.id.sp_cache_strategy);
        sp_cache_strategy.setPrompt("请选择缓存策略");
        sp_cache_strategy.setAdapter(modeAdapter);
        sp_cache_strategy.setSelection(0);
        sp_cache_strategy.setOnItemSelectedListener(new StrategySelectedListener());
    }

    private String[] strategyArray = {"自动选择缓存策略", "不缓存图片", "只缓存原始图片", "只缓存压缩后的图片", "同时缓存原图和压缩图片"};

    class StrategySelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mCacheStrategy = arg2;
            showNetworkImage(); // 加载并显示网络图片
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }

    // 加载并显示网络图片
    private void showNetworkImage() {
        // 构建一个加载网络图片的建造器
        RequestBuilder<Drawable> builder = Glide.with(this).load(mImageUrl);
        RequestOptions options = new RequestOptions(); // 创建Glide的请求选项
//        options.disallowHardwareConfig(); // 关闭硬件加速，防止过大尺寸的图片加载报错
        options.skipMemoryCache(true); // 是否跳过内存缓存（但不影响硬盘缓存）
        options.override(300, 200); // 设置图片的宽高
        if (ck_seize.isChecked()) { // 勾选了占位图
            options.placeholder(R.drawable.load_default); // 设置加载开始的占位图
        }
        if (ck_error.isChecked()) { // 勾选了出错图
            options.error(R.drawable.load_error); // 设置发生错误的提示图
        }
        if (ck_original.isChecked()) { // 勾选了原始图
            options.override(Target.SIZE_ORIGINAL); // 展示原始图片
        }
        if (ck_transition.isChecked()) { // 勾选了渐变动画
            builder.transition(DrawableTransitionOptions.withCrossFade(3000)); // 设置时长3秒的渐变动画
        }
        if (mCacheStrategy == 0) { // 自动选择缓存策略
            options.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC); // 设置指定的缓存策略
        } else if (mCacheStrategy == 1) { // 不缓存图片
            options.diskCacheStrategy(DiskCacheStrategy.NONE); // 设置指定的缓存策略
        } else if (mCacheStrategy == 2) { // 只缓存原始图片
            options.diskCacheStrategy(DiskCacheStrategy.DATA); // 设置指定的缓存策略
        } else if (mCacheStrategy == 3) { // 只缓存压缩后的图片
            options.diskCacheStrategy(DiskCacheStrategy.RESOURCE); // 设置指定的缓存策略
        } else if (mCacheStrategy == 4) { // 同时缓存原始图片和压缩图片
            options.diskCacheStrategy(DiskCacheStrategy.ALL); // 设置指定的缓存策略
        }
        // 在图像视图上展示网络图片。apply方法表示启用指定的请求选项
        builder.apply(options).into(iv_network);
    }

    // 先下载图片，再显示图片
    private void downloadImage(String url) {
        Glide.with(this).downloadOnly().load(url).listener(new RequestListener<File>() {
            @Override
            public boolean onLoadFailed(GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                Log.d(TAG, "local image path = "+resource.getAbsolutePath());
                iv_network.setImageURI(Uri.parse(resource.getAbsolutePath()));
                return false;
            }
        }).preload();
    }

}
