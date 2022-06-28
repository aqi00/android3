package com.example.chapter15;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.chapter15.adapter.PdfPageAdapter;
import com.example.chapter15.dao.BookDao;
import com.example.chapter15.entity.BookInfo;
import com.example.chapter15.util.BitmapUtil;
import com.example.chapter15.util.MD5Util;

import org.vudroid.core.DecodeService;
import org.vudroid.core.DecodeServiceBase;
import org.vudroid.core.DocumentView;
import org.vudroid.djvudroid.codec.DjvuContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DjvuRenderActivity extends AppCompatActivity {
    private final static String TAG = "DjvuRenderActivity";
    private ViewPager vp_content; // 声明一个翻页视图对象
    private FrameLayout fr_content; // 声明一个框架布局对象
    public static DecodeService decodeService; // 声明一个解码服务对象
    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private String mRootDir, mImgDir, mFileName; // 文件目录和文件路径
    private ProgressDialog mDialog; // 声明一个进度对话框对象
    private List<String> mFileNameList = new ArrayList<>(); // 图片路径列表
    private BookDao bookDao; // 声明一个书籍的持久化对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_djvu_render);
        initView(); // 初始化视图
        // 从App实例中获取唯一的书籍持久化对象
        bookDao = MainApplication.getInstance().getBookDB().bookDao();
        readBook(); // 开始读取书籍内容
    }

    // 初始化视图
    private void initView() {
        // 从前个页面传来的数据中获取书籍的标题和文件名称
        String title = getIntent().getStringExtra("title");
        mFileName = getIntent().getStringExtra("file_name");
        Toolbar tl_head = findViewById(R.id.tl_head);
        tl_head.setTitle(!TextUtils.isEmpty(title) ? title : mFileName);
        setSupportActionBar(tl_head); // 替换系统自带的ActionBar
        // 设置工具栏左侧导航图标的点击监听器
        tl_head.setNavigationOnClickListener(view -> finish());
        fr_content = findViewById(R.id.fr_content);
        vp_content = findViewById(R.id.vp_content);
        PagerTabStrip pts_tab = findViewById(R.id.pts_tab);
        // 设置翻页标题栏的文本大小
        pts_tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        decodeService.recycle(); // 回收解码服务
        decodeService = null;
    }

    // 开始读取书籍内容
    private void readBook() {
        // 弹出进度对话框
        mDialog = ProgressDialog.show(this, "请稍候", "正在努力加载");
        // 创建一个djvu的解码服务实例
        decodeService = new DecodeServiceBase(new DjvuContext());
        // 生成DJVU文件的图片保存目录
        mRootDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/djvu/";
        mImgDir = mRootDir + MD5Util.encrypt(mFileName);
        if (!new File(mImgDir).exists()) {
            new File(mImgDir).mkdirs();
        }
        // 创建一个文档视图
        DocumentView documentView = new DocumentView(this);
        // 设置文档视图的布局参数
        documentView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        // 设置解码服务的内容解析器
        decodeService.setContentResolver(getContentResolver());
        decodeService.setContainerView(documentView); // 设置解码服务的内容视图
        documentView.setDecodeService(decodeService); // 设置文档视图的解码服务
        fr_content.addView(documentView); // 把文档视图添加到框架布局上
        // 命令解码服务打开指定路径的电子书
        decodeService.open(Uri.fromFile(new File(mRootDir+mFileName)));
        Log.d(TAG, "getPageCount="+decodeService.getPageCount());
        // 根据电子书的总页数生成图片路径列表
        for (int i=0; i<decodeService.getPageCount(); i++) {
            String imgPath = String.format("%s/%03d.jpg", mImgDir, i);
            mFileNameList.add(imgPath);
        }
        BookInfo book = bookDao.queryBookByName(mFileName);
        book.setPageCount(decodeService.getPageCount());
        bookDao.updateBook(book); // 更新数据库中该书籍记录的总页数
        mHandler.postDelayed(mBookRender, 100); // 延迟100毫秒后启动书籍渲染任务
    }

    private int mIndex = 0; // 当前书页的序号
    // 定义一个书籍渲染任务
    private Runnable mBookRender = () -> {
        Log.d(TAG, "getBitmap mIndex="+mIndex);
        // 生成该页图片的保存路径
        final String imgPath = String.format("%s/%03d.jpg", mImgDir, mIndex);
        if (!(new File(imgPath)).exists()) { // 不存在该页的图片
            // 对该页内容进行解码处理
            decodeService.decodePage(mRootDir, mIndex, new DecodeService.DecodeCallback() {
                // 在解码完成时触发
                @Override
                public void decodeComplete(final Bitmap bitmap) {
                    BitmapUtil.saveImage(imgPath, bitmap); // 把位图数据保存为图片文件
                    Log.d(TAG, "getBitmap index="+mIndex+",imgPath="+imgPath+",bitmap.getByteCount="+bitmap.getByteCount());
                    doNext(); // 进行下一步处理
                }
            }, 1, new RectF(0, 0, 1, 1));
        } else { // 存在该页的图片
            doNext(); // 进行下一步处理
        }
    };

    // 进行下一步处理
    private void doNext() {
        mIndex++;
        // 先加载前两个图片，因为ViewPager+Fragment组合初始就是加载前两页。全部加载要花很多时间
        if (mIndex < 2 && mIndex < decodeService.getPageCount()) { // 是前两页
            mHandler.post(mBookRender); // 立即启动书籍渲染任务
        } else { // 不是前两页
            runOnUiThread(() -> showContent()); // 回到UI主线程显示书页内容
        }
    }

    // 显示书页内容
    private void showContent() {
        // 下面使用ViewPager展示每个书页图片
        PdfPageAdapter adapter = new PdfPageAdapter(getSupportFragmentManager(), mFileNameList);
        vp_content.setAdapter(adapter);
        vp_content.setCurrentItem(0);
        vp_content.setVisibility(View.VISIBLE);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss(); // 关闭进度对话框
        }
    }

}