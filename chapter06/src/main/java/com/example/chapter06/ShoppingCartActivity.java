package com.example.chapter06;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter06.dao.CartDao;
import com.example.chapter06.dao.GoodsDao;
import com.example.chapter06.entity.CartInfo;
import com.example.chapter06.entity.GoodsInfo;
import com.example.chapter06.util.FileUtil;
import com.example.chapter06.util.SharedUtil;
import com.example.chapter06.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("SetTextI18n")
public class ShoppingCartActivity extends AppCompatActivity {
    private final static String TAG = "ShoppingCartActivity";
    private TextView tv_count; // 声明一个文本视图对象
    private TextView tv_total_price; // 声明一个文本视图对象
    private LinearLayout ll_content; // 声明一个线性布局对象
    private LinearLayout ll_cart; // 声明一个购物车列表的线性布局对象
    private LinearLayout ll_empty; // 声明一个线性布局对象
    private CartDao cartDao; // 声明一个购物车的持久化对象
    private GoodsDao goodsDao; // 声明一个商品的持久化对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("购物车");
        tv_count = findViewById(R.id.tv_count);
        tv_total_price = findViewById(R.id.tv_total_price);
        ll_content = findViewById(R.id.ll_content);
        ll_cart = findViewById(R.id.ll_cart);
        ll_empty = findViewById(R.id.ll_empty);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_shopping_channel).setOnClickListener(v -> {
            // 从购物车页面跳到商场页面
            Intent intent = new Intent(this, ShoppingChannelActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 设置启动标志
            startActivity(intent); // 跳转到手机商场页面
        });
        findViewById(R.id.btn_clear).setOnClickListener(v -> {
            cartDao.deleteAllCart(); // 清空购物车数据库
            MainApplication.goodsCount = 0;
            showCount(); // 显示最新的商品数量
            ToastUtil.show(this, "购物车已清空");
        });
        findViewById(R.id.btn_settle).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("结算商品");
            builder.setMessage("客官抱歉，支付功能尚未开通，请下次再来");
            builder.setPositiveButton("我知道了", null);
            builder.create().show(); // 显示提醒对话框
        });
        // 从App实例中获取唯一的购物车持久化对象
        cartDao = MainApplication.getInstance().getCartDB().cartDao();
        // 从App实例中获取唯一的商品持久化对象
        goodsDao = MainApplication.getInstance().getGoodsDB().goodsDao();
        MainApplication.goodsCount = cartDao.queryAllCart().size();
    }

    // 显示购物车图标中的商品数量
    private void showCount() {
        tv_count.setText("" + MainApplication.goodsCount);
        if (MainApplication.goodsCount == 0) {
            ll_content.setVisibility(View.GONE);
            ll_cart.removeAllViews(); // 移除下面的所有子视图
            mGoodsMap.clear();
            ll_empty.setVisibility(View.VISIBLE);
        } else {
            ll_content.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCount(); // 显示购物车的商品数量
        downloadGoods(); // 模拟从网络下载商品图片
        showCart(); // 展示购物车中的商品列表
    }

    // 声明一个购物车中的商品信息列表
    private List<CartInfo> mCartList = new ArrayList<CartInfo>();
    // 声明一个根据商品编号查找商品信息的映射
    private final HashMap<Long, GoodsInfo> mGoodsMap = new HashMap<Long, GoodsInfo>();

    private void deleteGoods(CartInfo info) {
        MainApplication.goodsCount -= info.getCount();
        // 从购物车的数据库中删除商品
        cartDao.deleteOneCart(info.getGoodsId());
        // 从购物车的列表中删除商品
        for (int i = 0; i < mCartList.size(); i++) {
            if (info.getGoodsId() == mCartList.get(i).getGoodsId()) {
                mCartList.remove(i);
                break;
            }
        }
        showCount(); // 显示最新的商品数量
        ToastUtil.show(this, "已从购物车删除" + mGoodsMap.get(info.getGoodsId()).getName());
        mGoodsMap.remove(info.getGoodsId());
        refreshTotalPrice(); // 刷新购物车中所有商品的总金额
    }

    // 展示购物车中的商品列表
    private void showCart() {
        ll_cart.removeAllViews(); // 移除下面的所有子视图
        mCartList = cartDao.queryAllCart(); // 查询购物车数据库中所有的商品记录
        Log.d(TAG, "mCartList.size()=" + mCartList.size());
        if (mCartList == null || mCartList.size() <= 0) {
            return;
        }
        for (int i = 0; i < mCartList.size(); i++) {
            final CartInfo info = mCartList.get(i);
            // 根据商品编号查询商品数据库中的商品记录
            final GoodsInfo goods = goodsDao.queryGoodsById(info.getGoodsId());
            Log.d(TAG, "name=" + goods.getName() + ",price=" + goods.getPrice() + ",desc=" + goods.getDesc());
            mGoodsMap.put(info.getGoodsId(), goods);
            // 获取布局文件item_goods.xml的根视图
            View view = LayoutInflater.from(this).inflate(R.layout.item_cart, null);
            ImageView iv_thumb = view.findViewById(R.id.iv_thumb);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_desc = view.findViewById(R.id.tv_desc);
            TextView tv_count = view.findViewById(R.id.tv_count);
            TextView tv_price = view.findViewById(R.id.tv_price);
            TextView tv_sum = view.findViewById(R.id.tv_sum);
            // 给商品行添加点击事件。点击商品行跳到商品的详情页
            view.setOnClickListener(v -> {
                Intent intent = new Intent(this, ShoppingDetailActivity.class);
                intent.putExtra("goods_id", info.getGoodsId());
                startActivity(intent); // 跳到商品详情页面
            });
            // 给商品行添加长按事件。长按商品行就删除该商品
            view.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("是否从购物车删除"+goods.getName()+"？");
                builder.setPositiveButton("是", (dialog, which) -> {
                    ll_cart.removeView(v); // 移除当前视图
                    deleteGoods(info); // 删除该商品
                });
                builder.setNegativeButton("否", null);
                builder.create().show(); // 显示提醒对话框
                return true;
            });
            iv_thumb.setImageURI(Uri.parse(goods.getPicPath())); // 设置商品图片
            tv_name.setText(goods.getName()); // 设置商品名称
            tv_desc.setText(goods.getDesc()); // 设置商品描述
            tv_count.setText("" + info.getCount()); // 设置商品数量
            tv_price.setText("" + (int)goods.getPrice()); // 设置商品单价
            tv_sum.setText("" + (int)(info.getCount() * goods.getPrice())); // 设置商品总价
            ll_cart.addView(view); // 往购物车列表添加该商品行
        }
        refreshTotalPrice(); // 重新计算购物车中的商品总金额
    }

    // 重新计算购物车中的商品总金额
    private void refreshTotalPrice() {
        int total_price = 0;
        for (CartInfo info : mCartList) {
            GoodsInfo goods = mGoodsMap.get(info.getGoodsId());
            total_price += goods.getPrice() * info.getCount();
        }
        tv_total_price.setText("" + total_price);
    }

    private String mFirst = "true"; // 是否首次打开
    // 模拟网络数据，初始化数据库中的商品信息
    private void downloadGoods() {
        // 获取共享参数保存的是否首次打开参数
        mFirst = SharedUtil.getIntance(this).readString("first", "true");
        // 获取当前App的私有下载路径
        String path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/";
        if (mFirst.equals("true")) { // 如果是首次打开
            ArrayList<GoodsInfo> goodsList = GoodsInfo.getDefaultList(); // 模拟网络图片下载
            for (int i = 0; i < goodsList.size(); i++) {
                GoodsInfo info = goodsList.get(i);
                long id = goodsDao.insertOneGoods(info); // 往商品数据库插入一条该商品的记录
                info.setId(id);
                Bitmap pic = BitmapFactory.decodeResource(getResources(), info.getPicRes());
                String pic_path = path + id + ".jpg";
                FileUtil.saveImage(pic_path, pic); // 往存储卡保存商品图片
                info.setPicPath(pic_path);
                goodsDao.updateGoods(info); // 更新商品数据库中该商品记录的图片路径
            }
        }
        // 把是否首次打开写入共享参数
        SharedUtil.getIntance(this).writeString("first", "false");
    }

}
