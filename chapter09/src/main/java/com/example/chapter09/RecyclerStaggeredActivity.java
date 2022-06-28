package com.example.chapter09;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.chapter09.adapter.RecyclerStagAdapter;
import com.example.chapter09.bean.NewsInfo;
import com.example.chapter09.widget.SpacesDecoration;

public class RecyclerStaggeredActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_staggered);
        initRecyclerStaggered(); // 初始化瀑布流布局的循环视图
    }

    // 初始化瀑布流布局的循环视图
    private void initRecyclerStaggered() {
        // 从布局文件中获取名叫rv_staggered的循环视图
        RecyclerView rv_staggered = findViewById(R.id.rv_staggered);
        // 创建一个垂直方向的瀑布流布局管理器（每行3列）
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(
                3, RecyclerView.VERTICAL);
        rv_staggered.setLayoutManager(manager); // 设置循环视图的布局管理器
        // 构建一个服装列表的瀑布流适配器
        RecyclerStagAdapter adapter = new RecyclerStagAdapter(this, NewsInfo.getDefaultStag());
        adapter.setOnItemClickListener(adapter); // 设置瀑布流列表的点击监听器
        adapter.setOnItemLongClickListener(adapter); // 设置瀑布流列表的长按监听器
        rv_staggered.setAdapter(adapter);  // 设置循环视图的瀑布流适配器
        rv_staggered.setItemAnimator(new DefaultItemAnimator());  // 设置循环视图的动画效果
        rv_staggered.addItemDecoration(new SpacesDecoration(3));  // 设置循环视图的空白装饰
    }

}
