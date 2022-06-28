package com.example.chapter11;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.example.chapter11.constant.ImageList;
import com.example.chapter11.util.Utils;
import com.example.chapter11.widget.BannerPager;

@SuppressLint("DefaultLocale")
public class CustomScrollActivity extends AppCompatActivity {
    private static final String TAG = "CustomScrollActivity";
    private TextView tv_flipper; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scroll);
        tv_flipper = findViewById(R.id.tv_flipper);
        BannerPager banner = findViewById(R.id.banner_pager);
        LayoutParams params = (LayoutParams) banner.getLayoutParams();
        params.height = (int) (Utils.getScreenWidth(this) * 250f / 640f);
        banner.setLayoutParams(params); // 设置广告轮播条的布局参数
        banner.setImage(ImageList.getDefault()); // 设置广告轮播条的图片列表
        // 设置广告轮播条的横幅点击监听器
        banner.setOnBannerListener(position -> {
            String desc = String.format("您点击了第%d张图片", position + 1);
            tv_flipper.setText(desc);
        });
    }

}
