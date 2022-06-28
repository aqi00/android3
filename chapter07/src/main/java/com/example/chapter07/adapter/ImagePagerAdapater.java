package com.example.chapter07.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.example.chapter07.bean.GoodsInfo;

import java.util.ArrayList;
import java.util.List;

public class ImagePagerAdapater extends PagerAdapter {
    // 声明一个图像视图列表
    private List<ImageView> mViewList = new ArrayList<ImageView>();
    // 声明一个商品信息列表
    private List<GoodsInfo> mGoodsList = new ArrayList<GoodsInfo>();

    // 图像翻页适配器的构造方法，传入上下文与商品信息列表
    public ImagePagerAdapater(Context context, List<GoodsInfo> goodsList) {
        mGoodsList = goodsList;
        // 给每个商品分配一个专用的图像视图
        for (int i = 0; i < mGoodsList.size(); i++) {
            ImageView view = new ImageView(context); // 创建一个图像视图对象
            view.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            view.setImageResource(mGoodsList.get(i).pic);
            mViewList.add(view); // 把该商品的图像视图添加到图像视图列表
        }
    }

    // 获取页面项的个数
    public int getCount() {
        return mViewList.size();
    }

    // 判断当前视图是否来自指定对象
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // 从容器中销毁指定位置的页面
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }

    // 实例化指定位置的页面，并将其添加到容器中
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

    // 获得指定页面的标题文本
    public CharSequence getPageTitle(int position) {
        return mGoodsList.get(position).name;
    }

}
