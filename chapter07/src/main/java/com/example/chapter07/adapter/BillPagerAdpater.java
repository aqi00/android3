package com.example.chapter07.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chapter07.fragment.BillFragment;

public class BillPagerAdpater extends FragmentPagerAdapter {
    private int mYear; // 声明当前账单所处的年份

    // 碎片页适配器的构造方法
    public BillPagerAdpater(FragmentManager fm, int year) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mYear = year;
    }

    // 获取碎片Fragment的个数，一年有12个月
    public int getCount() {
        return 12;
    }

    // 获取指定月份的碎片Fragment
    public Fragment getItem(int position) {
        return BillFragment.newInstance(mYear*100 + (position + 1));
    }

    // 获得指定月份的标题文本
    public CharSequence getPageTitle(int position) {
        return (position + 1) + "月份";
    }

}
