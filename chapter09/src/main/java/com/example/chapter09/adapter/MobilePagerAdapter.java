package com.example.chapter09.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.chapter09.bean.GoodsInfo;
import com.example.chapter09.fragment.MobileFragment;

import java.util.ArrayList;
import java.util.List;

public class MobilePagerAdapter extends FragmentStateAdapter {
    private List<GoodsInfo> mGoodsList = new ArrayList<GoodsInfo>(); // 声明一个商品列表

    // 碎片页适配器的构造方法，传入碎片管理器与商品信息列表
    public MobilePagerAdapter(FragmentActivity fa, List<GoodsInfo> goodsList) {
        super(fa);
        mGoodsList = goodsList;
    }

    // 创建指定位置的碎片Fragmen
    @Override
    public Fragment createFragment(int position) {
        return MobileFragment.newInstance(position,
                mGoodsList.get(position).pic, mGoodsList.get(position).desc);
    }

    // 获取碎片Fragment的个数
    @Override
    public int getItemCount() {
        return mGoodsList.size();
    }
}

