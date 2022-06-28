package com.example.chapter09.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chapter09.R;
import com.example.chapter09.adapter.MobileGridAdapter;
import com.example.chapter09.adapter.RecyclerCombineAdapter;
import com.example.chapter09.adapter.RecyclerGridAdapter;
import com.example.chapter09.bean.GoodsInfo;
import com.example.chapter09.bean.NewsInfo;
import com.example.chapter09.util.Utils;
import com.example.chapter09.widget.BannerPager;
import com.example.chapter09.widget.SpacesDecoration;

import java.util.ArrayList;
import java.util.List;

public class DepartmentHomeFragment extends Fragment implements BannerPager.BannerClickListener {
    protected View mView; // 声明一个视图对象
    protected AppCompatActivity mActivity; // 声明一个活动对象

    private List<Integer> getImageList() {
        ArrayList<Integer> imageList = new ArrayList<Integer>();
        imageList.add(R.drawable.banner_1);
        imageList.add(R.drawable.banner_2);
        imageList.add(R.drawable.banner_3);
        imageList.add(R.drawable.banner_4);
        imageList.add(R.drawable.banner_5);
        return imageList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (AppCompatActivity) getActivity();
        mView = inflater.inflate(R.layout.fragment_department_home, container, false);
        // 从布局文件中获取名叫tl_head的工具栏
        Toolbar tl_head = mView.findViewById(R.id.tl_head);
        tl_head.setTitle("商城首页"); // 设置工具栏的标题文字
        mActivity.setSupportActionBar(tl_head); // 使用tl_head替换系统自带的ActionBar
        initBanner(); // 初始化广告轮播条
        initGrid(); // 初始化市场网格列表
        initCombine(); // 初始化猜你喜欢的商品展示网格
        initPhone(); // 初始化手机网格列表
        return mView;
    }

    private void initBanner() {
        // 从布局文件中获取名叫banner_pager的广告轮播条
        BannerPager banner = mView.findViewById(R.id.banner_pager);
        // 获取广告轮播条的布局参数
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) banner.getLayoutParams();
        params.height = (int) (Utils.getScreenWidth(mActivity) * 250f / 640f);
        banner.setLayoutParams(params); // 设置广告轮播条的布局参数
        banner.setImage(getImageList()); // 设置广告轮播条的广告图片列表
        banner.setOnBannerListener(this); // 设置广告轮播条的广告点击监听器
        banner.start(); // 开始轮播广告图片
    }

    // 一旦点击了广告图，就回调监听器的onBannerClick方法
    public void onBannerClick(int position) {
        String desc = String.format("您点击了第%d张图片", position + 1);
        Toast.makeText(mActivity, desc, Toast.LENGTH_LONG).show();
    }

    private void initGrid() {
        // 从布局文件中获取名叫rv_grid的循环视图
        RecyclerView rv_grid = mView.findViewById(R.id.rv_grid);
        // 创建一个网格布局管理器
        GridLayoutManager manager = new GridLayoutManager(mActivity, 5);
        rv_grid.setLayoutManager(manager); // 设置循环视图的布局管理器
        // 构建一个市场列表的网格适配器
        RecyclerGridAdapter adapter = new RecyclerGridAdapter(mActivity, NewsInfo.getDefaultGrid());
        adapter.setOnItemClickListener(adapter); // 设置网格列表的点击监听器
        adapter.setOnItemLongClickListener(adapter); // 设置网格列表的长按监听器
        rv_grid.setAdapter(adapter); // 设置循环视图的网格适配器
        rv_grid.setItemAnimator(new DefaultItemAnimator()); // 设置循环视图的动画效果
        rv_grid.addItemDecoration(new SpacesDecoration(1)); // 设置循环视图的空白装饰
    }

    private void initCombine() {
        // 从布局文件中获取名叫rv_combine的循环视图
        RecyclerView rv_combine = mView.findViewById(R.id.rv_combine);
        // 创建一个四列的网格布局管理器
        GridLayoutManager manager = new GridLayoutManager(mActivity, 4);
        // 设置网格布局管理器的占位规则
        // 以下占位规则的意思是：第一项和第二项占两列，其它项占一列；
        // 如果网格的列数为四，那么第一项和第二项平分第一行，第二行开始每行有四项。
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0 || position == 1) { // 为第一项或者第二项
                    return 2; // 占据两列
                } else { // 为其它项
                    return 1; // 占据一列
                }
            }
        });
        rv_combine.setLayoutManager(manager); // 设置循环视图的布局管理器
        // 构建一个猜你喜欢的网格适配器
        RecyclerCombineAdapter adapter = new RecyclerCombineAdapter(mActivity, NewsInfo.getDefaultCombine());
        adapter.setOnItemClickListener(adapter); // 设置网格列表的点击监听器
        adapter.setOnItemLongClickListener(adapter); // 设置网格列表的长按监听器
        rv_combine.setAdapter(adapter); // 设置循环视图的网格适配器
        rv_combine.setItemAnimator(new DefaultItemAnimator());  // 设置循环视图的动画效果
        rv_combine.addItemDecoration(new SpacesDecoration(1));  // 设置循环视图的空白装饰
    }

    private void initPhone() {
        // 从布局文件中获取名叫rv_phone的循环视图
        RecyclerView rv_phone = mView.findViewById(R.id.rv_phone);
        // 创建一个网格布局管理器
        GridLayoutManager manager = new GridLayoutManager(mActivity, 3);
        rv_phone.setLayoutManager(manager); // 设置循环视图的布局管理器
        // 构建一个手机列表的循环适配器
        MobileGridAdapter adapter = new MobileGridAdapter(mActivity, GoodsInfo.getDefaultList());
        rv_phone.setAdapter(adapter); // 设置循环视图的网格适配器
        adapter.setOnItemClickListener(adapter); // 设置网格列表的点击监听器
        adapter.setOnItemLongClickListener(adapter); // 设置网格列表的长按监听器
        rv_phone.setItemAnimator(new DefaultItemAnimator()); // 设置循环视图的动画效果
        rv_phone.addItemDecoration(new SpacesDecoration(1)); // 设置循环视图的空白装饰
    }

}
