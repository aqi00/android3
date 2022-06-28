package com.example.chapter15;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.chapter15.util.AssetsUtil;
import com.example.chapter15.util.BitmapUtil;
import com.example.chapter15.util.Utils;

import java.io.File;
import java.util.ArrayList;

import fi.harism.curl.CurlPage;
import fi.harism.curl.CurlView;

public class PdfOpenglActivity extends AppCompatActivity {
    private final static String TAG = "PdfOpenglActivity";
    private CurlView cv_content; // 声明一个卷曲视图对象
    private ArrayList<String> mImgList = new ArrayList<>(); // 图片路径列表
    private String mFileName = "tangshi.pdf"; // 演示文件的名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_opengl);
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
        cv_content = findViewById(R.id.cv_content);
    }

    // 开始渲染PDF文件
    private void renderPDF() {
        String dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/pdf/";
        String filePath = dir + mFileName;
        // 无法直接从asset目录读取PDF文件，只能先把PDF文件复制到存储卡，再从存储卡读取PDF
        AssetsUtil.Assets2Sd(this, mFileName, filePath);
        try {
            // 打开存储卡里指定路径的PDF文件
            ParcelFileDescriptor pfd = ParcelFileDescriptor.open(
                    new File(filePath), ParcelFileDescriptor.MODE_READ_ONLY);
            // 创建一个PDF渲染器
            PdfRenderer pdfRenderer = new PdfRenderer(pfd);
            Log.d(TAG, "page count=" + pdfRenderer.getPageCount());
            // 依次处理PDF文件的每个页面
            for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
                // 生成该页图片的保存路径
                String imgPath = String.format("%s/%03d.jpg", dir, i);
                mImgList.add(imgPath);
                // 打开序号为i的页面
                PdfRenderer.Page page = pdfRenderer.openPage(i);
                // 创建该页面的临时位图
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(),
                        Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(Color.WHITE); // 将临时位图洗白
                // 渲染该PDF页面并写入到临时位图
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                BitmapUtil.saveImage(imgPath, bitmap); // 把位图对象保存为图片文件
                page.close(); // 关闭该PDF页面
            }
            pdfRenderer.close(); // 处理完毕，关闭PDF渲染器
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        // 从指定路径的图片文件中获取位图数据
        Bitmap bitmap = BitmapFactory.decodeFile(mImgList.get(0));
        int iv_height = (int)(1.0*bitmap.getHeight()/bitmap.getWidth() * Utils.getScreenWidth(this));
        showImage(iv_height); // 在卷曲视图上显示位图图像
        bitmap.recycle(); // 回收位图对象
    }

    // 在卷曲视图上显示位图图像
    private void showImage(int height) {
        LayoutParams params = cv_content.getLayoutParams();
        params.height = height;
        // 设置卷曲视图的布局参数
        cv_content.setLayoutParams(params);
        // 设置卷曲视图的书页提供器
        cv_content.setPageProvider(new PageProvider(mImgList));
        // 设置卷曲视图的尺寸变更观察器
        cv_content.setSizeChangedObserver(new SizeChangedObserver());
        // 设置卷曲视图默认显示第一页
        cv_content.setCurrentIndex(0);
        // 设置卷曲视图的背景颜色
        cv_content.setBackgroundColor(Color.LTGRAY);
    }

    // 定义一个加载图片页面的提供器
    private class PageProvider implements CurlView.PageProvider {
        private ArrayList<String> mPathArray = new ArrayList<>();

        public PageProvider(ArrayList<String> pathArray) {
            mPathArray = pathArray;
        }

        @Override
        public int getPageCount() {
            return mPathArray.size();
        }

        // 在页面更新时触发
        public void updatePage(CurlPage page, int width, int height, int index) {
            // 加载指定页面的位图
            Bitmap front = BitmapFactory.decodeFile(mPathArray.get(index));
            // 设置书页的纹理
            page.setTexture(front, CurlPage.SIDE_BOTH);
        }
    }

    // 定义一个监听卷曲视图发生尺寸变更的观察器
    private class SizeChangedObserver implements CurlView.SizeChangedObserver {
        @Override
        public void onSizeChanged(int w, int h) {
            // 设置卷曲视图的观看模式
            cv_content.setViewMode(CurlView.SHOW_ONE_PAGE);
            // 设置卷曲视图的四周边缘
            cv_content.setMargins(0f, 0f, 0f, 0f);
        }
    }

}