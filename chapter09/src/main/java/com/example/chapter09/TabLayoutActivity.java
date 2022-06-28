package com.example.chapter09;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener;

import com.example.chapter09.adapter.GoodsPagerAdapter;
import com.example.chapter09.util.DateUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;

public class TabLayoutActivity extends AppCompatActivity implements OnTabSelectedListener {
    private final static String TAG = "TabLayoutActivity";
    private ViewPager vp_content; // 声明一个翻页视图对象
    private TabLayout tab_title; // 声明一个标签布局对象
    private String[] mTitleArray = {"商品", "详情"}; // 标题文字数组

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);
        Toolbar tl_head = findViewById(R.id.tl_head); // 从布局文件中获取名叫tl_head的工具栏
        tl_head.setTitle(""); // 设置工具栏的标题文本
        setSupportActionBar(tl_head); // 使用tl_head替换系统自带的ActionBar
        tab_title = findViewById(R.id.tab_title); // 从布局文件中获取名叫tab_title的标签布局
        vp_content = findViewById(R.id.vp_content); // 从布局文件中获取名叫vp_content的翻页视图
        initTabLayout(); // 初始化标签布局
        initTabViewPager(); // 初始化标签翻页
    }

    // 初始化标签布局
    private void initTabLayout() {
        // 给标签布局添加一个文字标签
        tab_title.addTab(tab_title.newTab().setText(mTitleArray[0]));
        // 给标签布局添加一个文字标签
        tab_title.addTab(tab_title.newTab().setText(mTitleArray[1]));
        tab_title.addOnTabSelectedListener(this); // 给标签布局添加标签选中监听器
        // 监听器ViewPagerOnTabSelectedListener允许直接关联某个翻页视图
        //tab_title.addOnTabSelectedListener(new ViewPagerOnTabSelectedListener(vp_content));
    }

    // 初始化标签翻页
    private void initTabViewPager() {
        // 构建一个商品信息的翻页适配器
        GoodsPagerAdapter adapter = new GoodsPagerAdapter(
                getSupportFragmentManager(), mTitleArray);
        vp_content.setAdapter(adapter); // 设置翻页视图的适配器
        // 给vp_content添加页面变更监听器
        vp_content.addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                tab_title.getTabAt(position).select(); // 选中指定位置的标签
            }
        });
    }

    // 在标签被重复选中时触发
    public void onTabReselected(Tab tab) {}

    // 在标签选中时触发
    public void onTabSelected(Tab tab) {
        vp_content.setCurrentItem(tab.getPosition()); // 设置翻页视图显示第几页
    }

    // 在标签取消选中时触发
    public void onTabUnselected(Tab tab) {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 从menu_overflow.xml中构建菜单界面布局
        getMenuInflater().inflate(R.menu.menu_overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) { // 点击了工具栏左边的返回箭头
            finish(); // 结束当前页面
        } else if (id == R.id.menu_refresh) { // 点击了刷新图标
            Toast.makeText(this, "当前刷新时间: " + DateUtil.getNowDateTime("yyyy-MM-dd HH:mm:ss")
                    , Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.menu_about) { // 点击了关于菜单项
            Toast.makeText(this, "这个是标签布局的演示demo", Toast.LENGTH_LONG).show();
            return true;
        } else if (id == R.id.menu_quit) { // 点击了退出菜单项
            finish(); // 结束当前页面
        }
        return super.onOptionsItemSelected(item);
    }

}
