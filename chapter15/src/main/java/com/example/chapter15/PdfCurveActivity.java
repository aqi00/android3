package com.example.chapter15;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.example.chapter15.util.AssetsUtil;
import com.example.chapter15.util.Utils;
import com.example.chapter15.widget.CurveView;

import java.util.ArrayList;
import java.util.List;

public class PdfCurveActivity extends AppCompatActivity {
    private final static String TAG = "PdfCurveActivity";
    private CurveView cv_book; // 声明一个卷曲视图对象
    private List<String> mPathList = new ArrayList<>(); // 图片路径列表
    private String mFileName = "tangshi.pdf"; // 演示文件的名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_curve);
        initView(); // 初始化视图
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
        cv_book = findViewById(R.id.cv_book);
        findViewById(R.id.btn_resume).setOnClickListener(v -> cv_book.reset());
    }

    // 开始渲染PDF文件
    private void renderPDF() {
        // 把资产文件转换为图片路径列表
        mPathList = AssetsUtil.getPathListFromPdf(this, mFileName);
        Log.d(TAG, "mPathList.size="+mPathList.size());
        Bitmap first = BitmapFactory.decodeFile(mPathList.get(0));
        int height = (int)(1.0*first.getHeight()/first.getWidth() * Utils.getScreenWidth(this));
        Log.d(TAG, "height="+height);
        ViewGroup.LayoutParams params = cv_book.getLayoutParams();
        params.height = height; // 根据书页图片的尺寸调整卷曲视图的高度
        cv_book.setLayoutParams(params); // 设置卷曲视图的布局参数
        cv_book.setFilePath(mPathList); // 设置卷曲视图的文件路径
    }

}