package com.example.chapter09.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;

import com.example.chapter09.R;
import com.example.chapter09.adapter.RecyclerStagAdapter;
import com.example.chapter09.bean.NewsInfo;
import com.example.chapter09.widget.SpacesDecoration;

import java.util.List;

public class ClothesFragment extends Fragment implements OnRefreshListener {
    private static final String TAG = "ClothesFragment";
    protected View mView; // 声明一个视图对象
    protected Context mContext; // 声明一个上下文对象
    private SwipeRefreshLayout srl_clothes; // 声明一个下拉刷新布局对象
    private RecyclerView rv_clothes; // 声明一个循环视图对象
    private RecyclerStagAdapter mAdapter; // 声明一个瀑布流适配器对象
    private List<NewsInfo> mAllList; // 服装信息列表

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity(); // 获取活动页面的上下文
        // 根据布局文件fragment_clothes.xml生成视图对象
        mView = inflater.inflate(R.layout.fragment_clothes, container, false);
        // 从布局文件中获取名叫srl_clothes的下拉刷新布局
        srl_clothes = mView.findViewById(R.id.srl_clothes);
        srl_clothes.setOnRefreshListener(this); // 设置下拉布局的下拉刷新监听器
        // 设置下拉布局的下拉变色资源数组
        srl_clothes.setColorSchemeResources(
                R.color.red, R.color.orange, R.color.green, R.color.blue);
        // 从布局文件中获取名叫rv_clothes的循环视图
        rv_clothes = mView.findViewById(R.id.rv_clothes);
        // 创建一个垂直方向的瀑布流布局管理器
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, RecyclerView.VERTICAL);
        rv_clothes.setLayoutManager(manager); // 设置循环视图的布局管理器
        mAllList = NewsInfo.getDefaultStag(); // 获取默认的服装信息列表
        // 构建一个服装列表的瀑布流适配器
        mAdapter = new RecyclerStagAdapter(mContext, mAllList);
        mAdapter.setOnItemClickListener(mAdapter); // 设置瀑布流列表的点击监听器
        mAdapter.setOnItemLongClickListener(mAdapter); // 设置瀑布流列表的长按监听器
        rv_clothes.setAdapter(mAdapter); // 设置循环视图的瀑布流适配器
        rv_clothes.setItemAnimator(new DefaultItemAnimator()); // 设置循环视图的动画效果
        rv_clothes.addItemDecoration(new SpacesDecoration(3)); // 设置循环视图的空白装饰
        return mView;
    }

    // 一旦在下拉刷新布局内部往下拉动页面，就触发下拉监听器的onRefresh方法
    public void onRefresh() {
        mHandler.postDelayed(mRefresh, 2000); // 延迟若干秒后启动刷新任务
    }

    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    // 定义一个刷新任务
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            srl_clothes.setRefreshing(false); // 结束下拉刷新布局的刷新动作
            // 更新服装信息列表
            for (int i = mAllList.size() - 1, count = 0; count < 5; count++) {
                NewsInfo item = mAllList.get(i);
                mAllList.remove(i);
                mAllList.add(0, item);
            }
            mAdapter.notifyDataSetChanged(); // 通知适配器的列表数据发生变化
            rv_clothes.scrollToPosition(0); // 让循环视图滚动到第一项所在的位置
        }
    };

}
