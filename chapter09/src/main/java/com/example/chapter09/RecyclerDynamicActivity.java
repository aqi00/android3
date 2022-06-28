package com.example.chapter09;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chapter09.adapter.LinearDynamicAdapter;
import com.example.chapter09.bean.NewsInfo;
import com.example.chapter09.widget.RecyclerExtras.OnItemClickListener;
import com.example.chapter09.widget.RecyclerExtras.OnItemDeleteClickListener;
import com.example.chapter09.widget.RecyclerExtras.OnItemLongClickListener;
import com.example.chapter09.widget.SpacesDecoration;

import java.util.List;
import java.util.Random;

@SuppressLint("DefaultLocale")
public class RecyclerDynamicActivity extends AppCompatActivity implements View.OnClickListener
        , OnItemClickListener, OnItemLongClickListener, OnItemDeleteClickListener {
    private RecyclerView rv_dynamic; // 声明一个循环视图对象
    private LinearDynamicAdapter mAdapter; // 声明一个线性适配器对象
    private List<NewsInfo> mPublicList = NewsInfo.getDefaultList(); // 当前的公众号信息列表
    private List<NewsInfo> mOriginList = NewsInfo.getDefaultList(); // 原始的公众号信息列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_dynamic);
        findViewById(R.id.btn_recycler_add).setOnClickListener(this);
        initRecyclerDynamic(); // 初始化动态线性布局的循环视图
    }

    // 初始化动态线性布局的循环视图
    private void initRecyclerDynamic() {
        // 从布局文件中获取名叫rv_dynamic的循环视图
        rv_dynamic = findViewById(R.id.rv_dynamic);
        // 创建一个垂直方向的线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(
                this, RecyclerView.VERTICAL, false);
        rv_dynamic.setLayoutManager(manager); // 设置循环视图的布局管理器
        // 构建一个公众号列表的线性适配器
        mAdapter = new LinearDynamicAdapter(this, mPublicList);
        mAdapter.setOnItemClickListener(this); // 设置线性列表的点击监听器
        mAdapter.setOnItemLongClickListener(this); // 设置线性列表的长按监听器
        mAdapter.setOnItemDeleteClickListener(this); // 设置线性列表的删除按钮监听器
        rv_dynamic.setAdapter(mAdapter); // 设置循环视图的线性适配器
        rv_dynamic.setItemAnimator(new DefaultItemAnimator());  // 设置循环视图的动画效果
        rv_dynamic.addItemDecoration(new SpacesDecoration(1));  // 设置循环视图的空白装饰
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_recycler_add) {
            int position = new Random().nextInt(mOriginList.size()-1); // 获取一个随机位置
            NewsInfo old_item = mOriginList.get(position);
            NewsInfo new_item = new NewsInfo(old_item.pic_id, old_item.title, old_item.desc);
            mPublicList.add(0, new_item); // 在顶部添加一条公众号消息
            mAdapter.notifyItemInserted(0); // 通知适配器列表在第一项插入数据
            rv_dynamic.scrollToPosition(0); // 让循环视图滚动到第一项所在的位置
        }
    }

    // 一旦点击循环适配器的列表项，就触发点击监听器的onItemClick方法
    public void onItemClick(View view, int position) {
        String desc = String.format("您点击了第%d项，标题是%s", position + 1,
                mPublicList.get(position).title);
        Toast.makeText(this, desc, Toast.LENGTH_SHORT).show();
    }

    // 一旦长按循环适配器的列表项，就触发长按监听器的onItemLongClick方法
    public void onItemLongClick(View view, int position) {
        NewsInfo item = mPublicList.get(position);
        item.isPressed = !item.isPressed;
        mPublicList.set(position, item);
        mAdapter.notifyItemChanged(position); // 通知适配器列表在第几项发生变更
    }

    // 一旦点击循环适配器列表项的删除按钮，就触发删除监听器的onItemDeleteClick方法
    public void onItemDeleteClick(View view, int position) {
        mPublicList.remove(position);
        mAdapter.notifyItemRemoved(position); // 通知适配器列表在第几项删除数据
    }

}
