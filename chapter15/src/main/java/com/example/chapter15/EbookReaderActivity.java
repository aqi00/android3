package com.example.chapter15;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.chapter15.adapter.BookListAdapter;
import com.example.chapter15.dao.BookDao;
import com.example.chapter15.entity.BookInfo;
import com.example.chapter15.util.AssetsUtil;
import com.example.chapter15.widget.InputDialog;

import java.util.ArrayList;
import java.util.List;

public class EbookReaderActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = "EbookReaderActivity";
    private ListView lv_ebook; // 声明一个用于展示书籍列表的列表视图对象
    private String[] mFileNameArray = {"tangshi.pdf", "android.pdf", "lunyu.epub", "zhugeliang.djvu", "dufu.djvu", "luyou.djvu"};
    private List<BookInfo> mBookList = new ArrayList<>(); // 书籍信息列表
    private BookListAdapter mAdapter; // 声明一个书籍列表的适配器对象
    private BookDao bookDao; // 声明一个书籍的持久化对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebook_reader);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕常亮
        initView(); // 初始化视图
        new Thread(() -> copySampleFile()).start(); // 启动演示文件的复制线程
    }

    // 初始化视图
    private void initView() {
        Toolbar tl_head = findViewById(R.id.tl_head);
        tl_head.setTitle("电子书架");
        setSupportActionBar(tl_head); // 替换系统自带的ActionBar
        // 设置工具栏左侧导航图标的点击监听器
        tl_head.setNavigationOnClickListener(view -> finish());
        lv_ebook = findViewById(R.id.lv_ebook);
    }

    // 把assets目录下的演示文件复制到存储卡
    private void copySampleFile() {
        // 从App实例中获取唯一的书籍持久化对象
        bookDao = MainApplication.getInstance().getBookDB().bookDao();
        mBookList = bookDao.queryAllBook(); // 获取所有书籍记录
        if (mBookList!=null && mBookList.size()>0) {
            runOnUiThread(() -> initBookList()); // 初始化书籍列表
            return;
        }
        List<BookInfo> bookList = new ArrayList<>();
        for (String file_name : mFileNameArray) {
            String dir = String.format("%s/%s/",
                    getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),
                    file_name.substring(file_name.lastIndexOf(".")+1)
            );
            String fileName = file_name.substring(file_name.lastIndexOf("/") + 1);
            // 把资产目录下的电子书复制到存储卡
            AssetsUtil.Assets2Sd(this, fileName, dir + fileName);
            bookList.add(new BookInfo(file_name));
        }
        bookDao.insertBookList(bookList); // 把演示用的电子书信息添加到数据库
        runOnUiThread(() -> initBookList()); // 初始化书籍列表
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initBookList(); // 初始化书籍列表
    }

    // 初始化书籍列表
    private void initBookList() {
        mBookList = bookDao.queryAllBook(); // 获取所有书籍记录
        // 下面把书籍列表通过ListView展现出来
        mAdapter = new BookListAdapter(this, mBookList);
        lv_ebook.setAdapter(mAdapter);
        lv_ebook.setOnItemClickListener(this);
        lv_ebook.setOnItemLongClickListener(this);
    }

    // 在点击书籍记录时触发
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String file_name = mBookList.get(position).getFileName();
        String title = mBookList.get(position).getTitle();
        Log.d(TAG, "file_name="+file_name+", title="+title);
        if (file_name.endsWith(".pdf")) { // PDF格式
            // 跳转到PDF阅读界面
            startReader(file_name, title, PdfRenderActivity.class);
        } else if (file_name.endsWith(".epub")) { // EPUB格式
            startReader(file_name, title, EpubReaderActivity.class);
        } else if (file_name.endsWith(".djvu")) { // DJVU格式
            // 跳转到第三方Vudroid提供的阅读界面
            startReader(file_name, title, DjvuRenderActivity.class);
        } else {
            Toast.makeText(this, "暂不支持该格式的电子书", Toast.LENGTH_SHORT).show();
        }
    }

    // 在长按书籍记录时触发
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // 以下创建并弹出标题填写对话框
        InputDialog dialog = new InputDialog(this, mBookList.get(position).getFileName(),
                position, "请输入书籍名称", (idt, content, seq) -> {
            BookInfo book = mBookList.get(seq);
            book.setTitle(content);
            bookDao.updateBook(book); // 更新数据库中该书籍记录的标题
        });
        dialog.show();
        return true;
    }

    // 启动指定的电子书阅读界面
    private void startReader(String file_name, String title, Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.putExtra("file_name", file_name);
        intent.putExtra("title", title);
        startActivity(intent);
    }

}