package com.example.chapter09.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chapter09.fragment.TabFirstFragment;
import com.example.chapter09.fragment.TabSecondFragment;
import com.example.chapter09.fragment.TabThirdFragment;

public class TabPagerAdapter extends FragmentPagerAdapter {

    // 碎片页适配器的构造方法，传入碎片管理器
    public TabPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    // 获取指定位置的碎片Fragment
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new TabFirstFragment();  // 返回第一个碎片
        } else if (position == 1) {
            return new TabSecondFragment();  // 返回第二个碎片
        } else if (position == 2) {
            return new TabThirdFragment();  // 返回第三个碎片
        } else {
            return null;
        }
    }

    // 获取碎片Fragment的个数
    @Override
    public int getCount() {
        return 3;
    }
}
