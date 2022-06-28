package com.example.chapter14.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.chapter14.bean.VideoInfo;
import com.example.chapter14.fragment.VideoFragment;

import java.util.List;

public class VideoPagerAdapter extends FragmentStateAdapter {
    private List<VideoInfo> mVideoList; // 声明一个地址列表

    // 碎片页适配器的构造方法，传入碎片管理器与商品信息列表
    public VideoPagerAdapter(FragmentActivity fa, List<VideoInfo> videoList) {
        super(fa);
        mVideoList = videoList;
    }

    // 创建指定位置的碎片Fragmen
    @Override
    public Fragment createFragment(int position) {
        return VideoFragment.newInstance(position, mVideoList.get(position));
    }

    // 获取碎片Fragment的个数
    @Override
    public int getItemCount() {
        return mVideoList.size();
    }
}
