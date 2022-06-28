package com.example.chapter09.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chapter09.R;
import com.example.chapter09.bean.GoodsInfo;

import java.util.ArrayList;
import java.util.List;

public class MobileRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = "MobileRecyclerAdapter";
    private Context mContext; // 声明一个上下文对象
    private List<GoodsInfo> mGoodsList = new ArrayList<GoodsInfo>(); // 声明一个商品列表

    public MobileRecyclerAdapter(Context context, List<GoodsInfo> goodsList) {
        mContext = context;
        mGoodsList = goodsList;
    }

    // 获取列表项的个数
    public int getItemCount() {
        return mGoodsList.size();
    }

    // 创建列表项的视图持有者
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int viewType) {
        // 根据布局文件item_mobile.xml生成视图对象
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_mobile, vg, false);
        return new ItemHolder(v);
    }

    // 绑定列表项的视图持有者
    public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position) {
        ItemHolder holder = (ItemHolder) vh;
        holder.iv_pic.setImageResource(mGoodsList.get(position).pic);
        holder.tv_desc.setText(mGoodsList.get(position).desc);
    }

    // 获取列表项的类型
    public int getItemViewType(int position) {
        return 0;
    }

    // 获取列表项的编号
    public long getItemId(int position) {
        return position;
    }

    // 定义列表项的视图持有者
    public class ItemHolder extends RecyclerView.ViewHolder {
        public ImageView iv_pic; // 声明列表项图标的图像视图
        public TextView tv_desc; // 声明列表项描述的文本视图

        public ItemHolder(View v) {
            super(v);
            iv_pic = v.findViewById(R.id.iv_pic);
            tv_desc = v.findViewById(R.id.tv_desc);
        }
    }

}
