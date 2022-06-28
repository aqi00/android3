package com.example.chapter07;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter07.adapter.GoodsAdapter;
import com.example.chapter07.adapter.GoodsAdapter.addCartListener;
import com.example.chapter07.bean.GoodsInfo;
import com.example.chapter07.database.CartDBHelper;
import com.example.chapter07.database.GoodsDBHelper;
import com.example.chapter07.util.ToastUtil;

import java.util.List;

@SuppressLint("SetTextI18n")
public class ShoppingChannelActivity extends AppCompatActivity implements View.OnClickListener, addCartListener {
    private final static String TAG = "ShoppingChannelActivity";
    private TextView tv_count; // 声明一个文本视图对象
    private GridView gv_channel; // 声明一个网格视图对象
    private GoodsDBHelper mGoodsHelper; // 声明一个商品数据库的帮助器对象
    private CartDBHelper mCartHelper; // 声明一个购物车数据库的帮助器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_channel);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("手机商场");
        tv_count = findViewById(R.id.tv_count);
        // 从布局视图中获取名叫gv_channel的网格视图
        gv_channel = findViewById(R.id.gv_channel);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_cart).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) { // 点击了返回图标
            finish(); // 关闭当前页面
        } else if (v.getId() == R.id.iv_cart) { // 点击了购物车图标
            // 从商场页面跳到购物车页面
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 设置启动标志
            startActivity(intent); // 跳转到购物车页面
        }
    }

    // 把指定编号的商品添加到购物车
    @Override
    public void addToCart(long goods_id, String goods_name) {
        MainApplication.goodsCount++;
        tv_count.setText("" + MainApplication.goodsCount);
        mCartHelper.save(goods_id); // 把该商品填入购物车数据库
        ToastUtil.show(this, "已添加一部" + goods_name + "到购物车");
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_count.setText("" + MainApplication.goodsCount);
        // 获取商品数据库的帮助器对象
        mGoodsHelper = GoodsDBHelper.getInstance(this, 1);
        mGoodsHelper.openReadLink(); // 打开商品数据库的读连接
        // 获取购物车数据库的帮助器对象
        mCartHelper = CartDBHelper.getInstance(this, 1);
        mCartHelper.openWriteLink(); // 打开购物车数据库的写连接
        showGoods(); // 展示商品列表
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoodsHelper.closeLink(); // 关闭商品数据库的数据库连接
        mCartHelper.closeLink(); // 关闭购物车数据库的数据库连接
    }

    private void showGoods() {
        // 模拟从网络上下载图片，从而构建简单的图片缓存机制
        ShoppingCartActivity.downloadGoods(this, mGoodsHelper);
        // 查询商品数据库中的所有商品记录
        List<GoodsInfo> goodsArray = mGoodsHelper.query("1=1");
        // 构建商场中商品网格的适配器对象
        GoodsAdapter adapter = new GoodsAdapter(this, goodsArray, this);
        // 给gv_channel设置商品网格适配器
        gv_channel.setAdapter(adapter);
        // 给gv_channel设置网格项点击监听器
        gv_channel.setOnItemClickListener(adapter);
    }

}
