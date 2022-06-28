package com.example.chapter09;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chapter09.adapter.RecyclerLinearAdapter;
import com.example.chapter09.bean.NewsInfo;
import com.example.chapter09.widget.SpacesDecoration;

public class RecyclerLinearActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_linear);
        initRecyclerLinear(); // 初始化线性布局的循环视图
    }

    // 初始化线性布局的循环视图
    private void initRecyclerLinear() {
        // 从布局文件中获取名叫rv_linear的循环视图
        RecyclerView rv_linear = findViewById(R.id.rv_linear);
        // 创建一个垂直方向的线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_linear.setLayoutManager(manager); // 设置循环视图的布局管理器
        // 构建一个公众号列表的线性适配器
        RecyclerLinearAdapter adapter = new RecyclerLinearAdapter(this, NewsInfo.getDefaultList());
        rv_linear.setAdapter(adapter);  // 设置循环视图的线性适配器
        rv_linear.setItemAnimator(new DefaultItemAnimator());  // 设置循环视图的动画效果
        rv_linear.addItemDecoration(new SpacesDecoration(1));  // 设置循环视图的空白装饰
    }

}
