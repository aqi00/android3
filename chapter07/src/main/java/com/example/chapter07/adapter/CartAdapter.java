package com.example.chapter07.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chapter07.R;
import com.example.chapter07.bean.CartInfo;

import java.util.List;

@SuppressLint("SetTextI18n")
public class CartAdapter extends BaseAdapter {
    private Context mContext; // 声明一个上下文对象
    private List<CartInfo> mCartList; // 声明一个购物车信息列表

    // 购物车适配器的构造方法，传入上下文、购物车里的商品列表
    public CartAdapter(Context context, List<CartInfo> cart_list) {
        mContext = context;
        mCartList = cart_list;
    }

    // 获取列表项的个数
    public int getCount() {
        return mCartList.size();
    }

    // 获取列表项的数据
    public Object getItem(int arg0) {
        return mCartList.get(arg0);
    }

    // 获取列表项的编号
    public long getItemId(int arg0) {
        return arg0;
    }

    // 获取指定位置的列表项视图
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) { // 转换视图为空
            holder = new ViewHolder(); // 创建一个新的视图持有者
            // 根据布局文件item_cart.xml生成转换视图对象
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_cart, null);
            holder.iv_thumb = convertView.findViewById(R.id.iv_thumb);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_desc = convertView.findViewById(R.id.tv_desc);
            holder.tv_count = convertView.findViewById(R.id.tv_count);
            holder.tv_price = convertView.findViewById(R.id.tv_price);
            holder.tv_sum = convertView.findViewById(R.id.tv_sum);
            // 将视图持有者保存到转换视图当中
            convertView.setTag(holder);
        } else { // 转换视图非空
            // 从转换视图中获取之前保存的视图持有者
            holder = (ViewHolder) convertView.getTag();
        }
        CartInfo info = mCartList.get(position);
        holder.iv_thumb.setImageURI(Uri.parse(info.goods.pic_path)); // 设置商品图片
        holder.tv_name.setText(info.goods.name); // 显示商品的名称
        holder.tv_desc.setText(info.goods.desc); // 显示商品的描述
        holder.tv_count.setText("" + info.count); // 显示商品的数量
        holder.tv_price.setText("" + (int) info.goods.price); // 显示商品的单价
        holder.tv_sum.setText("" + (int) (info.count * info.goods.price)); // 显示商品的总价
        return convertView;
    }

    // 定义一个视图持有者，以便重用列表项的视图资源
    public final class ViewHolder {
        public ImageView iv_thumb; // 声明商品图片的图像视图对象
        public TextView tv_name; // 声明商品名称的文本视图对象
        public TextView tv_desc; // 声明商品描述的文本视图对象
        public TextView tv_count; // 声明商品数量的文本视图对象
        public TextView tv_price; // 声明商品单价的文本视图对象
        public TextView tv_sum; // 声明商品总价的文本视图对象
    }

}
