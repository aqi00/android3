package com.example.chapter15;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.chapter15.bean.ParseResponse;
import com.example.chapter15.constant.UrlConstant;
import com.example.chapter15.dao.BookDao;
import com.example.chapter15.entity.BookInfo;
import com.example.chapter15.util.FileUtil;
import com.example.chapter15.util.OfficeUtil;
import com.example.chapter15.util.ToastUtil;

import java.io.IOException;

public class ReadWordActivity extends AppCompatActivity {
    private static final String TAG = "ReadWordActivity";
    public final static String URL_PARSE = UrlConstant.HTTP_PREFIX + "parseDoc";
    private String mFileName; // 文件名称
    private WebView wv_content; // 声明一个网页视图对象
    private ProgressDialog mDialog; // 声明一个进度对话框对象
    private BookDao bookDao; // 声明一个书籍的持久化对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_word);
        initView(); // 初始化视图
        // 从App实例中获取唯一的书籍持久化对象
        bookDao = MainApplication.getInstance().getBookDB().bookDao();
        // 注册一个善后工作的活动结果启动器，获取指定类型的文档
        ActivityResultLauncher launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                // 弹出进度对话框
                mDialog = ProgressDialog.show(this, "请稍候", "正在努力加载");
                new Thread(() -> importDOC(uri)).start(); // 启动doc文件的导入线程
            }
        });
        findViewById(R.id.btn_choose).setOnClickListener(v -> launcher.launch("application/*"));
    }

    // 初始化视图
    private void initView() {
        wv_content = findViewById(R.id.wv_content);
        // 给网页视图设置默认的网页浏览客户端
        wv_content.setWebViewClient(new WebViewClient());
        // 获取网页视图的网页设置
        WebSettings settings = wv_content.getSettings();
        // 设置是否支持缩放
        settings.setSupportZoom(true);
        // 设置是否出现缩放工具
        settings.setBuiltInZoomControls(true);
        // 当容器超过页面大小时，是否放大页面大小到容器宽度
        settings.setUseWideViewPort(true);
        // 当页面超过容器大小时，是否缩小页面尺寸到页面宽度
        settings.setLoadWithOverviewMode(true);
    }

    // 从数据库中加载文档
    private boolean loadLocalFile() {
        BookInfo book = bookDao.queryBookByName(mFileName); // 查询文档信息
        if (book != null) {
            runOnUiThread(() -> { // 回到UI线程展示PPT页面
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss(); // 关闭进度对话框
                }
                Log.d(TAG, "url: "+UrlConstant.HTTP_PREFIX +book.getPath());
                // 命令网页视图加载指定路径的网页
                wv_content.loadUrl(UrlConstant.HTTP_PREFIX + book.getPath());
            });
            return true; // 找到文档
        } else {
            return false; // 未找到文档
        }
    }

    // 从指定的文件路径导入文档
    private void importDOC(Uri uri) {
        String filePath = FileUtil.getPathFromContentUri(this, uri);
        Log.d(TAG, "filePath="+filePath);
        mFileName = filePath.substring(filePath.lastIndexOf("/")+1);
        if (!loadLocalFile()) { // 数据库未找到该文档
            // 向服务器上传文档，异步返回文档解析结果
            OfficeUtil.uploadDocument(this, filePath, URL_PARSE, new OfficeUtil.UploadListener() {
                @Override
                public void onFail(IOException e) { // 解析失败
                    mDialog.dismiss(); // 关闭进度对话框
                    ToastUtil.show(ReadWordActivity.this, "文档解析异常："+e.getMessage());
                }

                @Override
                public void onSucc(ParseResponse parseResponse) { // 解析成功
                    mDialog.dismiss(); // 关闭进度对话框
                    // 命令网页视图加载指定路径的网页
                    wv_content.loadUrl(UrlConstant.HTTP_PREFIX + parseResponse.getHtmlPath());
                    // 下面把html文件路径保存至数据库
                    BookInfo book = new BookInfo(mFileName);
                    book.setPath(parseResponse.getHtmlPath());
                    bookDao.insertOneBook(book);
                }
            });
        }
    }

}