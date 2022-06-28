package com.example.chapter15;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chapter15.adapter.PdfPageAdapter;
import com.example.chapter15.bean.ParseResponse;
import com.example.chapter15.constant.UrlConstant;
import com.example.chapter15.dao.BookDao;
import com.example.chapter15.entity.BookInfo;
import com.example.chapter15.util.BitmapUtil;
import com.example.chapter15.util.FileUtil;
import com.example.chapter15.util.MD5Util;
import com.example.chapter15.util.OfficeUtil;
import com.example.chapter15.util.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("CheckResult")
public class ReadPptActivity extends AppCompatActivity {
    private static final String TAG = "ReadPptActivity";
    public final static String URL_PARSE = UrlConstant.HTTP_PREFIX + "parsePpt";
    private String mFileName; // 文件名称
    private ViewPager vp_content; // 声明一个翻页视图对象
    private ProgressDialog mDialog; // 声明一个进度对话框对象
    private BookDao bookDao; // 声明一个书籍的持久化对象
    private String mPPTDir; // ppt保存目录
    private int mSuccCount; // 请求成功的图片数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_ppt);
        vp_content = findViewById(R.id.vp_content);
        PagerTabStrip pts_tab = findViewById(R.id.pts_tab);
        // 设置翻页标题栏的文本大小
        pts_tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        // 从App实例中获取唯一的书籍持久化对象
        bookDao = MainApplication.getInstance().getBookDB().bookDao();
        // 注册一个善后工作的活动结果启动器，获取指定类型的文档
        ActivityResultLauncher launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                // 弹出进度对话框
                mDialog = ProgressDialog.show(this, "请稍候", "正在努力加载");
                new Thread(() -> importPPT(uri)).start(); // 启动ppt文件的导入线程
            }
        });
        findViewById(R.id.btn_choose).setOnClickListener(v -> launcher.launch("application/*"));
    }

    // 加载本地保存的图片文件
    private boolean loadLocalFile() {
        mPPTDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString()
                + "/ppt/" + MD5Util.encrypt(mFileName);
        if (!new File(mPPTDir).exists()) {
            new File(mPPTDir).mkdirs();
        }
        Log.d(TAG, "mPPTDir="+mPPTDir);
        BookInfo book = bookDao.queryBookByName(mFileName); // 查询文档信息
        if (book != null) {
            List<String> pathList = new ArrayList<>();
            for (int i=0; i<book.getPageCount(); i++) {
                String imagePath = String.format("%s/%03d.jpg", mPPTDir, i);
                pathList.add(imagePath);
            }
            PdfPageAdapter adapter = new PdfPageAdapter(getSupportFragmentManager(), pathList);
            runOnUiThread(() -> { // 回到UI线程展示PPT页面
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss(); // 关闭进度对话框
                }
                vp_content.setAdapter(adapter);
            });
            return true; // 找到文档
        } else {
            return false; // 未找到文档
        }
    }

    // 从指定的文件路径导入ppt文件
    private void importPPT(Uri uri) {
        String filePath = FileUtil.getPathFromContentUri(this, uri);
        mFileName = filePath.substring(filePath.lastIndexOf("/")+1);
        Log.d(TAG, "filePath="+filePath);
        Log.d(TAG, "mFileName="+mFileName);
        if (!loadLocalFile()) { // 本地未找到解析后的图片
            // 向服务器上传文档，异步返回文档解析结果
            OfficeUtil.uploadDocument(this, filePath, URL_PARSE, new OfficeUtil.UploadListener() {
                @Override
                public void onFail(IOException e) { // 解析失败
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.dismiss(); // 关闭进度对话框
                    }
                    ToastUtil.show(ReadPptActivity.this, "文档解析异常："+e.getMessage());
                }

                @Override
                public void onSucc(ParseResponse parseResponse) { // 解析成功
                    showPPT(parseResponse); // 显示服务器返回的ppt图片
                }
            });
        }
    }

    // 显示服务器返回的ppt图片
    private void showPPT(ParseResponse parseResponse) {
        List<String> pathList = new ArrayList<>();
        for (String path : parseResponse.getPathList()) {
            pathList.add(UrlConstant.HTTP_PREFIX + path);
        }
        PdfPageAdapter adapter = new PdfPageAdapter(getSupportFragmentManager(), pathList);
        vp_content.setAdapter(adapter);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss(); // 关闭进度对话框
        }
        // 启动线程保存服务器返回的ppt图片
        new Thread(() -> savePptImage(pathList)).start();
    }

    // 保存服务器返回的ppt图片
    private void savePptImage(List<String> pathList) {
        Log.d(TAG, "savePptImage pathList.size="+pathList.size());
        mSuccCount = 0;
        for (int i=0; i<pathList.size(); i++) {
            String imagePath = String.format("%s/%03d.jpg", mPPTDir, i);
            OfficeUtil.downloadImage(this, pathList.get(i), imagePath, new OfficeUtil.DownloadListener() {
                @Override
                public void onFail(IOException e) {}

                @Override
                public void onSucc(String imagePath) {
                    mSuccCount++;
                    // 下面把ppt文件页数保存至数据库
                    if (mSuccCount == pathList.size()) {
                        BookInfo book = new BookInfo(mFileName);
                        book.setPageCount(pathList.size());
                        bookDao.insertOneBook(book);
                    }
                }
            });
        }
    }
}