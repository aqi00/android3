package com.example.chapter06;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter06.dao.CartDao;
import com.example.chapter06.dao.GoodsDao;
import com.example.chapter06.entity.GoodsInfo;
import com.example.chapter06.util.ToastUtil;
import com.example.chapter06.util.Utils;

import java.util.List;

@SuppressLint("SetTextI18n")
public class ShoppingChannelActivity extends AppCompatActivity {
    private TextView tv_count; // 声明一个文本视图对象
    private GridLayout gl_channel; // 声明一个商品频道的网格布局对象
    private CartDao cartDao; // 声明一个购物车的持久化对象
    private GoodsDao goodsDao; // 声明一个商品的持久化对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_channel);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("手机商场");
        tv_count = findViewById(R.id.tv_count);
        gl_channel = findViewById(R.id.gl_channel);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        findViewById(R.id.iv_cart).setOnClickListener(v -> {
            // 从商场页面跳到购物车页面
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 设置启动标志
            startActivity(intent); // 跳转到购物车页面
        });
        tv_count.setText("" + MainApplication.goodsCount);
        // 从App实例中获取唯一的购物车持久化对象
        cartDao = MainApplication.getInstance().getCartDB().cartDao();
        // 从App实例中获取唯一的商品持久化对象
        goodsDao = MainApplication.getInstance().getGoodsDB().goodsDao();
    }

    // 把指定编号的商品添加到购物车
    private void addToCart(long goods_id, String goods_name) {
        MainApplication.goodsCount++;
        tv_count.setText("" + MainApplication.goodsCount);
        cartDao.save(goods_id); // 把该商品填入购物车数据库
        ToastUtil.show(this, "已添加一部" + goods_name + "到购物车");
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_count.setText("" + MainApplication.goodsCount);
        showGoods(); // 展示商品列表
    }

    private void showGoods() {
        int screenWidth = Utils.getScreenWidth(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                screenWidth/2, LinearLayout.LayoutParams.WRAP_CONTENT);
        gl_channel.removeAllViews(); // 移除下面的所有子视图
        // 查询商品数据库中的所有商品记录
        List<GoodsInfo> goodsList = goodsDao.queryAllGoods();
        for (final GoodsInfo info : goodsList) {
            // 获取布局文件item_goods.xml的根视图
            View view = LayoutInflater.from(this).inflate(R.layout.item_goods, null);
            ImageView iv_thumb = view.findViewById(R.id.iv_thumb);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_price = view.findViewById(R.id.tv_price);
            Button btn_add = view.findViewById(R.id.btn_add);
            tv_name.setText(info.getName()); // 设置商品名称
            iv_thumb.setImageURI(Uri.parse(info.getPicPath())); // 设置商品图片
            iv_thumb.setOnClickListener(v -> {
                Intent intent = new Intent(this, ShoppingDetailActivity.class);
                intent.putExtra("goods_id", info.getId());
                startActivity(intent); // 跳到商品详情页面
            });
            tv_price.setText("" + (int)info.getPrice()); // 设置商品价格
            btn_add.setOnClickListener(v -> addToCart(info.getId(), info.getName()));
            gl_channel.addView(view, params); // 把商品视图添加到网格布局
        }
    }

}
