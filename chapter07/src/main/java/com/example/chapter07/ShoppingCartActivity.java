package com.example.chapter07;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chapter07.adapter.CartAdapter;
import com.example.chapter07.bean.CartInfo;
import com.example.chapter07.bean.GoodsInfo;
import com.example.chapter07.database.CartDBHelper;
import com.example.chapter07.database.GoodsDBHelper;
import com.example.chapter07.util.FileUtil;
import com.example.chapter07.util.SharedUtil;
import com.example.chapter07.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("SetTextI18n")
public class ShoppingCartActivity extends Activity implements
        OnClickListener, OnItemClickListener, OnItemLongClickListener {
    private final static String TAG = "ShoppingCartActivity";
    private TextView tv_count; // 声明一个文本视图对象
    private TextView tv_total_price; // 声明一个文本视图对象
    private LinearLayout ll_content; // 声明一个线性布局对象
    private LinearLayout ll_empty; // 声明一个线性布局对象
    private ListView lv_cart; // 声明一个列表视图对象
    private CartAdapter mCartAdapter; // 声明一个购物车适配器
    private GoodsDBHelper mGoodsHelper; // 声明一个商品数据库的帮助器对象
    private CartDBHelper mCartHelper; // 声明一个购物车数据库的帮助器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("购物车");
        tv_count = findViewById(R.id.tv_count);
        tv_total_price = findViewById(R.id.tv_total_price);
        ll_content = findViewById(R.id.ll_content);
        ll_empty = findViewById(R.id.ll_empty);
        // 从布局视图中获取名叫lv_cart的列表视图
        lv_cart = findViewById(R.id.lv_cart);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.btn_shopping_channel).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_settle).setOnClickListener(this);
    }

    // 显示购物车图标中的商品数量
    private void showCount() {
        tv_count.setText("" + MainApplication.goodsCount);
        if (MainApplication.goodsCount == 0) {
            ll_content.setVisibility(View.GONE);
            mCartArray.clear();
            if (mCartAdapter != null) {
                mCartAdapter.notifyDataSetChanged(); // 通知适配器发生了数据变化
            }
            mGoodsMap.clear();
            ll_empty.setVisibility(View.VISIBLE);
        } else {
            ll_content.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) { // 点击了返回图标
            finish(); // 关闭当前页面
        } else if (v.getId() == R.id.btn_shopping_channel) { // 点击了“商场”按钮
            // 从购物车页面跳到商场页面
            Intent intent = new Intent(this, ShoppingChannelActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 设置启动标志
            startActivity(intent); // 跳转到手机商场页面
        } else if (v.getId() == R.id.btn_clear) { // 点击了“清空”按钮
            mCartHelper.deleteAll(); // 清空购物车数据库
            MainApplication.goodsCount = 0;
            showCount(); // 显示最新的商品数量
            ToastUtil.show(this, "购物车已清空");
        } else if (v.getId() == R.id.btn_settle) { // 点击了“结算”按钮
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("结算商品");
            builder.setMessage("客官抱歉，支付功能尚未开通，请下次再来");
            builder.setPositiveButton("我知道了", null);
            builder.create().show(); // 显示提醒对话框
        }
    }

    // 商品项的点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CartInfo info = mCartArray.get(position);
        Intent intent = new Intent(this, ShoppingDetailActivity.class);
        intent.putExtra("goods_id", info.goods_id);
        startActivity(intent); // 跳到商品详情页面
    }

    // 商品项的长按事件
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final CartInfo info = mCartArray.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingCartActivity.this);
        builder.setMessage("是否从购物车删除"+info.goods.name+"？");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCartArray.remove(position);
                mCartAdapter.notifyDataSetChanged(); // 通知适配器发生了数据变化
                deleteGoods(info); // 删除该商品
            }
        });
        builder.setNegativeButton("否", null);
        builder.create().show(); // 显示提醒对话框
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCount(); // 显示购物车的商品数量
        // 获取商品数据库的帮助器对象
        mGoodsHelper = GoodsDBHelper.getInstance(this, 1);
        mGoodsHelper.openWriteLink(); // 打开商品数据库的写连接
        // 获取购物车数据库的帮助器对象
        mCartHelper = CartDBHelper.getInstance(this, 1);
        mCartHelper.openWriteLink(); // 打开购物车数据库的写连接
        downloadGoods(this, mGoodsHelper); // 模拟从网络下载商品图片
        showCart(); // 展示购物车中的商品列表
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoodsHelper.closeLink(); // 关闭商品数据库的数据库连接
        mCartHelper.closeLink(); // 关闭购物车数据库的数据库连接
    }

    // 声明一个购物车中的商品信息列表
    private List<CartInfo> mCartArray = new ArrayList<CartInfo>();
    // 声明一个根据商品编号查找商品信息的映射
    private HashMap<Long, GoodsInfo> mGoodsMap = new HashMap<Long, GoodsInfo>();

    private void deleteGoods(CartInfo info) {
        MainApplication.goodsCount -= info.count;
        // 从购物车的数据库中删除商品
        mCartHelper.delete("goods_id=" + info.goods_id);
        // 从购物车的列表中删除商品
        for (int i = 0; i < mCartArray.size(); i++) {
            if (info.goods_id == mCartArray.get(i).goods_id) {
                mCartArray.remove(i);
                break;
            }
        }
        showCount(); // 显示最新的商品数量
        ToastUtil.show(this, "已从购物车删除" + mGoodsMap.get(info.goods_id).name);
        mGoodsMap.remove(info.goods_id);
        refreshTotalPrice(); // 刷新购物车中所有商品的总金额
    }

    // 展示购物车中的商品列表
    private void showCart() {
        mCartArray = mCartHelper.query("1=1"); // 查询购物车数据库中所有的商品记录
        if (mCartArray == null || mCartArray.size() <= 0) {
            return;
        }
        for (int i = 0; i < mCartArray.size(); i++) {
            CartInfo info = mCartArray.get(i);
            // 根据商品编号查询商品数据库中的商品记录
            GoodsInfo goods = mGoodsHelper.queryById(info.goods_id);
            Log.d(TAG, "name=" + goods.name + ",price=" + goods.price + ",desc=" + goods.desc);
            mGoodsMap.put(info.goods_id, goods);
            info.goods = goods;
            mCartArray.set(i, info); // 补充商品记录的商品详情
        }
        // 构建购物车商品列表的适配器对象
        mCartAdapter = new CartAdapter(this, mCartArray);
        lv_cart.setAdapter(mCartAdapter); // 给lv_cart设置商品列表适配器
        lv_cart.setOnItemClickListener(this); // 给lv_cart设置列表项点击监听器
        lv_cart.setOnItemLongClickListener(this); // 给lv_cart设置列表项长按监听器
        refreshTotalPrice(); // 重新计算购物车中的商品总金额
    }

    // 重新计算购物车中的商品总金额
    private void refreshTotalPrice() {
        int total_price = 0;
        for (CartInfo info : mCartArray) {
            GoodsInfo goods = mGoodsMap.get(info.goods_id);
            total_price += goods.price * info.count;
        }
        tv_total_price.setText("" + total_price);
    }

    private static String mFirst = "true"; // 是否首次打开
    // 模拟网络数据，初始化数据库中的商品信息
    public static void downloadGoods(Context ctx, GoodsDBHelper helper) {
        // 获取共享参数保存的是否首次打开参数
        mFirst = SharedUtil.getIntance(ctx).readString("first", "true");
        // 获取当前App的私有下载路径
        String path = ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/";
        if (mFirst.equals("true")) { // 如果是首次打开
            List<GoodsInfo> goodsList = GoodsInfo.getDefaultList(); // 模拟网络图片下载
            for (int i = 0; i < goodsList.size(); i++) {
                GoodsInfo info = goodsList.get(i);
                long rowid = helper.insert(info); // 往商品数据库插入一条该商品的记录
                info.rowid = rowid;
                Bitmap pic = BitmapFactory.decodeResource(ctx.getResources(), info.pic);
                String pic_path = path + rowid + ".jpg";
                FileUtil.saveImage(pic_path, pic); // 往存储卡保存商品图片
                pic.recycle(); // 回收位图对象
                info.pic_path = pic_path;
                helper.update(info); // 更新商品数据库中该商品记录的图片路径
            }
        }
        // 把是否首次打开写入共享参数
        SharedUtil.getIntance(ctx).writeString("first", "false");
    }

}
