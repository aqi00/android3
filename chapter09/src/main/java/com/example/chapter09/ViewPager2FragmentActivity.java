package com.example.chapter09;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.chapter09.adapter.MobilePagerAdapter;
import com.example.chapter09.bean.GoodsInfo;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class ViewPager2FragmentActivity extends AppCompatActivity {
    private List<GoodsInfo> mGoodsList = GoodsInfo.getDefaultList(); // 商品信息列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager2_fragment);
        // 从布局文件中获取名叫tab_title的标签布局
        TabLayout tab_title = findViewById(R.id.tab_title);
        // 从布局文件中获取名叫vp2_content的二代翻页视图
        ViewPager2 vp2_content = findViewById(R.id.vp2_content);
        // 构建一个商品信息的翻页适配器
        MobilePagerAdapter adapter = new MobilePagerAdapter(this, mGoodsList);
        vp2_content.setAdapter(adapter); // 设置二代翻页视图的适配器
        // 把标签布局跟翻页视图通过指定策略连为一体，二者在页面切换时一起联动
        new TabLayoutMediator(tab_title, vp2_content, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(TabLayout.Tab tab, int position) {
                tab.setText(mGoodsList.get(position).name); // 设置每页的标签文字
            }
        }).attach();
    }
}
