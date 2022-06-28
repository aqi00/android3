package com.example.chapter09.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.chapter09.R;
import com.example.chapter09.adapter.ClassPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class DepartmentClassFragment extends Fragment {
    protected View mView; // 声明一个视图对象
    protected AppCompatActivity mActivity; // 声明一个活动对象
    private List<String> mTitleList = new ArrayList<String>(); // 标题文字列表

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (AppCompatActivity) getActivity();
        mView = inflater.inflate(R.layout.fragment_department_class, container, false);
        // 从布局文件中获取名叫tl_head的工具栏
        Toolbar tl_head = mView.findViewById(R.id.tl_head);
        mActivity.setSupportActionBar(tl_head); // 使用tl_head替换系统自带的ActionBar
        mTitleList.add("服装");
        mTitleList.add("电器");
        // 从布局文件中获取名叫tab_title的标签布局
        TabLayout tab_title = mView.findViewById(R.id.tab_title);
        // 从布局文件中获取名叫vp2_content的二代翻页视图
        ViewPager2 vp2_content = mView.findViewById(R.id.vp2_content);
        // 构建一个分类信息的翻页适配器。注意Fragment嵌套时要传getChildFragmentManager
        ClassPagerAdapter adapter = new ClassPagerAdapter(mActivity, mTitleList);
        vp2_content.setAdapter(adapter); // 设置二代翻页视图的适配器
        // 把标签布局跟翻页视图通过指定策略连为一体，二者在页面切换时一起联动
        new TabLayoutMediator(tab_title, vp2_content, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(TabLayout.Tab tab, int position) {
                tab.setText(mTitleList.get(position)); // 设置每页的标签文字
            }
        }).attach();
        return mView;
    }

}
