package com.example.chapter15;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import com.example.chapter15.adapter.PdfPageAdapter;
import com.example.chapter15.dao.BookDao;
import com.example.chapter15.entity.BookInfo;
import com.example.chapter15.util.AssetsUtil;

import java.util.ArrayList;
import java.util.List;

public class PdfRenderActivity extends AppCompatActivity {
    private final static String TAG = "PdfRenderActivity";
    private List<String> mPathList = new ArrayList<>(); // 图片路径列表
    private String mFileName = "tangshi.pdf"; // 演示文件的名称
    private ViewPager vp_content; // 声明一个翻页视图对象
    private BookDao bookDao; // 声明一个书籍的持久化对象
    private ProgressDialog mDialog; // 声明一个进度对话框对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_render);
        initView(); // 初始化视图
        // 从App实例中获取唯一的书籍持久化对象
        bookDao = MainApplication.getInstance().getBookDB().bookDao();
        // 弹出进度对话框
        mDialog = ProgressDialog.show(this, "请稍候", "正在努力加载");
        new Thread(() -> importPDF()).start(); // 启动pdf文件的导入线程
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

    // 从指定的资产文件导入pdf文件
    private void importPDF() {
        mPathList = AssetsUtil.getPathListFromPdf(this, mFileName);
        Log.d(TAG, "mPathList.size="+mPathList.size());
        BookInfo book = bookDao.queryBookByName(mFileName);
        if (book != null) {
            book.setPageCount(mPathList.size());
            bookDao.updateBook(book); // 更新数据库中该书籍记录的总页数
        }
        // 回到主线程显示导入后的pdf各页面
        runOnUiThread(() -> {
            PdfPageAdapter adapter = new PdfPageAdapter(getSupportFragmentManager(), mPathList);
            vp_content.setAdapter(adapter);
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss(); // 关闭进度对话框
            }
        });
    }

    // 在创建选项菜单时调用
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book, menu);
        return true;
    }

    // 在选中菜单项时调用
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_slide) { // 点击了“平滑翻页”
            Intent intent = new Intent(this, PdfSlideActivity.class);
            intent.putExtra("file_name", mFileName);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_curve) { // 点击了“卷曲翻页”
            Intent intent = new Intent(this, PdfCurveActivity.class);
            intent.putExtra("file_name", mFileName);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menu_opengl) { // 点击了“OpenGL翻页”
            Intent intent = new Intent(this, PdfOpenglActivity.class);
            intent.putExtra("file_name", mFileName);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}