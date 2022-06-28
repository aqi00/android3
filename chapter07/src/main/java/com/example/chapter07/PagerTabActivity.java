package com.example.chapter07;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.example.chapter07.adapter.ImagePagerAdapater;
import com.example.chapter07.bean.GoodsInfo;

import java.util.List;

public class PagerTabActivity extends AppCompatActivity implements OnPageChangeListener {
    private List<GoodsInfo> mGoodsList; // 手机商品列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager_tab);
        initPagerStrip(); // 初始化翻页标签栏
        initViewPager(); // 初始化翻页视图
    }

    // 初始化翻页标签栏
    private void initPagerStrip() {
        // 从布局视图中获取名叫pts_tab的翻页标签栏
        PagerTabStrip pts_tab = findViewById(R.id.pts_tab);
        // 设置翻页标签栏的文本大小
        pts_tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        pts_tab.setTextColor(Color.BLACK); // 设置翻页标签栏的文本颜色
    }

    // 初始化翻页视图
    private void initViewPager() {
        mGoodsList = GoodsInfo.getDefaultList();
        // 构建一个商品图片的翻页适配器
        ImagePagerAdapater adapter = new ImagePagerAdapater(this, mGoodsList);
        // 从布局视图中获取名叫vp_content的翻页视图
        ViewPager vp_content = findViewById(R.id.vp_content);
        vp_content.setAdapter(adapter); // 设置翻页视图的适配器
        vp_content.setCurrentItem(0); // 设置翻页视图显示第一页
        vp_content.addOnPageChangeListener(this); // 给翻页视图添加页面变更监听器
    }

    // 翻页状态改变时触发。state取值说明为：0表示静止，1表示正在滑动，2表示滑动完毕
    // 在翻页过程中，状态值变化依次为：正在滑动→滑动完毕→静止
    public void onPageScrollStateChanged(int state) {}

    // 在翻页过程中触发。该方法的三个参数取值说明为 ：第一个参数表示当前页面的序号
    // 第二个参数表示当前页面偏移的百分比，取值为0到1；第三个参数表示当前页面的偏移距离
    public void onPageScrolled(int position, float ratio, int offset) {}

    // 在翻页结束后触发。position表示当前滑到了哪一个页面
    public void onPageSelected(int position) {
        Toast.makeText(this, "您翻到的手机品牌是：" + mGoodsList.get(position).name, Toast.LENGTH_SHORT).show();
    }
}
