package com.example.chapter15;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.example.chapter15.util.AssetsUtil;
import com.example.chapter15.widget.ViewSlider;

import java.util.ArrayList;
import java.util.List;

public class PdfSlideActivity extends AppCompatActivity {
    private final static String TAG = "PdfSliderActivity";
    private List<String> mPathList = new ArrayList<>(); // 图片路径列表
    private String mFileName = "tangshi.pdf"; // 演示文件的名称
    private ViewSlider vs_content; // 声明一个滑动视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_slide);
        initView(); // 初始化视图
        vs_content = findViewById(R.id.vs_content);
        // 加载pdf会花一点点时间，这里先让整个界面出来，再慢慢渲染pdf
        new Handler(Looper.myLooper()).post(() -> renderPDF());
    }

    // 初始化视图
    private void initView() {
        String title = "";
        // 从前个页面传来的数据中获取书籍的标题和文件名称
        if (getIntent().getExtras()!=null && !getIntent().getExtras().isEmpty()) {
            title = getIntent().getStringExtra("title");
            mFileName = getIntent().getStringExtra("file_name");
        }
        Toolbar tl_head = findViewById(R.id.tl_head);
        tl_head.setTitle(!TextUtils.isEmpty(title) ? title : mFileName);
        setSupportActionBar(tl_head); // 替换系统自带的ActionBar
        // 设置工具栏左侧导航图标的点击监听器
        tl_head.setNavigationOnClickListener(view -> finish());
        vs_content = findViewById(R.id.vs_content);
    }

    // 开始渲染PDF文件
    private void renderPDF() {
        mPathList = AssetsUtil.getPathListFromPdf(this, mFileName);
        Log.d(TAG, "mPathList.size="+mPathList.size());
        vs_content.setFilePath(mPathList); // 给滑动视图设置图片路径列表
    }

}