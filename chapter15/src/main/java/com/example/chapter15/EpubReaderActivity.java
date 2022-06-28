package com.example.chapter15;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import com.example.chapter15.adapter.EpubPagerAdapter;
import com.example.chapter15.dao.BookDao;
import com.example.chapter15.entity.BookInfo;
import com.example.chapter15.util.FileUtil;
import com.example.chapter15.util.MD5Util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.epub.EpubReader;

public class EpubReaderActivity extends AppCompatActivity {
    private final static String TAG = "EpubReaderActivity";
    private String mFileName = "lunyu.epub";
    private String mDir;
    private ViewPager vp_content; // 声明一个翻页视图对象
    private BookDao bookDao; // 声明一个书籍的持久化对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub_reader);
        initView(); // 初始化视图
        // 生成电子书解析后的文件存放目录
        mDir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() +
                "/epub/" + MD5Util.encrypt(mFileName);
        // 从App实例中获取唯一的书籍持久化对象
        bookDao = MainApplication.getInstance().getBookDB().bookDao();
        new Handler(Looper.myLooper()).post(() -> loadEpub());
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
        vp_content = findViewById(R.id.vp_content);
        PagerTabStrip pts_tab = findViewById(R.id.pts_tab);
        // 设置翻页标题栏的文本大小
        pts_tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
    }

    // 加载EPUB文件
    private void loadEpub() {
        // 创建一个EPUB阅读器对象
        EpubReader epubReader = new EpubReader();
        Book book = null;
        // 打开assets目录下的资产文件
        try (InputStream is = getAssets().open(mFileName)) {
            // 从输入流中读取书籍数据
            book = epubReader.readEpub(is);
            Log.d(TAG, "getTitle="+book.getTitle());
            Log.d(TAG, "getAuthors="+book.getMetadata().getAuthors());
            // 设置书籍的概要描述
            setBookMeta(book, book.getTitle());
            // 获取该书的所有资源，包括网页、图片等等
            Resources resources = book.getResources();
            // 获取所有的链接地址
            Collection<String> hrefArray = resources.getAllHrefs();
            for (String href : hrefArray) {
                // 获取该链接指向的资源
                Resource res = resources.getByHref(href);
                // 把资源的字节数组保存为文件
                FileUtil.writeFile(mDir + "/" + href, res.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> htmlList = new ArrayList<>();
        // 获取该书的所有内容页，也就是所有网页
        List<Resource> contents = book.getContents();
        Log.d(TAG, "size="+contents.size());
        for (int i = 0; i < contents.size(); i++) {
            // 获取该网页的链接地址，并添加到网页列表中
            String href = String.format("%s/%s", mDir, contents.get(i).getHref());
            htmlList.add(href);
        }
        // 下面使用ViewPager展示每页的WebView内容
        EpubPagerAdapter adapter = new EpubPagerAdapter(getSupportFragmentManager(), htmlList);
        vp_content.setAdapter(adapter);
        vp_content.setCurrentItem(0);
    }

    // 设置书籍的概要描述
    private void setBookMeta(Book book, String mainTitle) {
        // 书籍的头部信息，可获取标题、语言、作者、封面等信息
        Metadata meta = book.getMetadata();
        // 获取该书的主标题
        String title = meta.getFirstTitle();
        if (TextUtils.isEmpty(title)) {
            title = mainTitle;
        }
        if (TextUtils.isEmpty(title)) {
            title = mFileName;
        }
        // 获取该书的页数，同时更新数据库中该书信息
        BookInfo info = bookDao.queryBookByName(mFileName);
        if (info != null) {
            info.setTitle(title);
            info.setPageCount(book.getContents().size());
            bookDao.updateBook(info); // 更新数据库中该书籍记录的总页数
        }
    }

}