package com.example.chapter06;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter06.dao.CartDao;
import com.example.chapter06.dao.GoodsDao;
import com.example.chapter06.entity.GoodsInfo;
import com.example.chapter06.util.ToastUtil;

@SuppressLint("SetTextI18n")
public class ShoppingDetailActivity extends AppCompatActivity {
    private TextView tv_title; // 声明一个文本视图对象
    private TextView tv_count; // 声明一个文本视图对象
    private TextView tv_goods_price; // 声明一个文本视图对象
    private TextView tv_goods_desc; // 声明一个文本视图对象
    private ImageView iv_goods_pic; // 声明一个图像视图对象
    private long mGoodsId; // 当前商品的商品编号
    private CartDao cartDao; // 声明一个购物车的持久化对象
    private GoodsDao goodsDao; // 声明一个商品的持久化对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_detail);
        tv_title = findViewById(R.id.tv_title);
        tv_count = findViewById(R.id.tv_count);
        tv_goods_price = findViewById(R.id.tv_goods_price);
        tv_goods_desc = findViewById(R.id.tv_goods_desc);
        iv_goods_pic = findViewById(R.id.iv_goods_pic);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        findViewById(R.id.iv_cart).setOnClickListener(v -> {
            startActivity(new Intent(this, ShoppingCartActivity.class)); // 跳转到购物车页面
        });
        findViewById(R.id.btn_add_cart).setOnClickListener(v -> addToCart(mGoodsId));
        tv_count.setText("" + MainApplication.goodsCount);
        // 从App实例中获取唯一的购物车持久化对象
        cartDao = MainApplication.getInstance().getCartDB().cartDao();
        // 从App实例中获取唯一的商品持久化对象
        goodsDao = MainApplication.getInstance().getGoodsDB().goodsDao();
    }

    // 把指定编号的商品添加到购物车
    private void addToCart(long goods_id) {
        MainApplication.goodsCount++;
        tv_count.setText("" + MainApplication.goodsCount);
        cartDao.save(goods_id); // 把该商品填入购物车数据库
        ToastUtil.show(this, "成功添加至购物车");
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDetail(); // 展示商品详情
    }

    private void showDetail() {
        // 获取上一个页面传来的商品编号
        mGoodsId = getIntent().getLongExtra("goods_id", 0L);
        if (mGoodsId > 0) {
            // 根据商品编号查询商品数据库中的商品记录
            GoodsInfo info = goodsDao.queryGoodsById(mGoodsId);
            tv_title.setText(info.getName()); // 设置商品名称
            tv_goods_desc.setText(info.getDesc()); // 设置商品描述
            tv_goods_price.setText("" + (int)info.getPrice()); // 设置商品价格
            iv_goods_pic.setImageURI(Uri.parse(info.getPicPath())); // 设置商品图片
        }
    }

}
