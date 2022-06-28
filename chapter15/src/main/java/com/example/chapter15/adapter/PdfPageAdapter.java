package com.example.chapter15.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.chapter15.fragment.ImageFragment;

import java.util.List;

public class PdfPageAdapter extends FragmentStatePagerAdapter {
    private List<String> mPathList; // 声明一个文件路径列表

    public PdfPageAdapter(FragmentManager fm, List<String> pathList) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mPathList = pathList;
    }

    public int getCount() {
        return mPathList.size();
    }

    public Fragment getItem(int position) {
        return ImageFragment.newInstance(mPathList.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "第" + (position + 1) + "页";
    }

}
