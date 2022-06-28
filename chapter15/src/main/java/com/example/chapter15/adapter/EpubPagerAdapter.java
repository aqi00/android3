package com.example.chapter15.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chapter15.fragment.HtmlFragment;

import java.util.List;

public class EpubPagerAdapter extends FragmentPagerAdapter {
    private List<String> mHtmlList; // 网页文件的路径列表

    public EpubPagerAdapter(FragmentManager fm, List<String> htmlArray) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mHtmlList = htmlArray;
    }

    @Override
    public int getCount() {
        return mHtmlList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return HtmlFragment.newInstance(mHtmlList.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "封面";
        } else {
            return "第" + position + "页";
        }
    }

}
