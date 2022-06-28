package com.example.chapter09.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chapter09.fragment.BookCoverFragment;
import com.example.chapter09.fragment.BookDetailFragment;

public class GoodsPagerAdapter extends FragmentPagerAdapter {
    private String[] mTitleArray; // 声明一个标题文字数组

    // 碎片页适配器的构造方法，传入碎片管理器与标题列表
    public GoodsPagerAdapter(FragmentManager fm, String[] titleArray) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mTitleArray = titleArray;
    }

    // 获取指定位置的碎片Fragment
    public Fragment getItem(int position) {
        if (position == 0) { // 第一页展示书籍封面
            return new BookCoverFragment();
        } else if (position == 1) { // 第二页展示书籍详情
            return new BookDetailFragment();
        }
        return new BookCoverFragment();
    }

    // 获取碎片Fragment的个数
    public int getCount() {
        return mTitleArray.length;
    }

    // 获得指定碎片页的标题文本
    public CharSequence getPageTitle(int position) {
        return mTitleArray[position];
    }
}
