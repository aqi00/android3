package com.example.chapter09.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chapter09.R;
import com.example.chapter09.bean.GoodsInfo;
import com.example.chapter09.widget.RecyclerExtras;

import java.util.List;

public class MobileGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        RecyclerExtras.OnItemClickListener, RecyclerExtras.OnItemLongClickListener {
    private final static String TAG = "MobileGridAdapter";
    private Context mContext; // 声明一个上下文对象
    private List<GoodsInfo> mGoodsList;

    public MobileGridAdapter(Context context, List<GoodsInfo> goodsList) {
        mContext = context;
        mGoodsList = goodsList;
    }

    // 获取列表项的个数
    public int getItemCount() {
        return mGoodsList.size();
    }

    // 创建列表项的视图持有者
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int viewType) {
        // 根据布局文件item_grid.xml生成视图对象
        View v = LayoutInflater.from(mContext).inflate(R.layout.grid_mobile, vg, false);
        return new MobileGridAdapter.ItemHolder(v);
    }

    // 绑定列表项的视图持有者
    public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position) {
        MobileGridAdapter.ItemHolder holder = (MobileGridAdapter.ItemHolder) vh;
        holder.iv_pic.setImageResource(mGoodsList.get(position).pic);
        holder.tv_title.setText(mGoodsList.get(position).name);
        // 列表项的点击事件需要自己实现
        holder.ll_item.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });
        // 列表项的长按事件需要自己实现
        holder.ll_item.setOnLongClickListener(v -> {
            if (mOnItemLongClickListener != null) {
                mOnItemLongClickListener.onItemLongClick(v, position);
            }
            return true;
        });
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
        public LinearLayout ll_item; // 声明列表项的线性布局
        public ImageView iv_pic; // 声明列表项图标的图像视图
        public TextView tv_title; // 声明列表项标题的文本视图

        public ItemHolder(View v) {
            super(v);
            ll_item = v.findViewById(R.id.ll_item);
            iv_pic = v.findViewById(R.id.iv_pic);
            tv_title = v.findViewById(R.id.tv_title);
        }
    }

    // 声明列表项的点击监听器对象
    private RecyclerExtras.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(RecyclerExtras.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    // 声明列表项的长按监听器对象
    private RecyclerExtras.OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemLongClickListener(RecyclerExtras.OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    // 处理列表项的点击事件
    public void onItemClick(View view, int position) {
        String desc = String.format("您点击了第%d项，商品名称是%s", position + 1,
                mGoodsList.get(position).name);
        Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
    }

    // 处理列表项的长按事件
    public void onItemLongClick(View view, int position) {
        String desc = String.format("您长按了第%d项，商品名称是%s", position + 1,
                mGoodsList.get(position).name);
        Toast.makeText(mContext, desc, Toast.LENGTH_SHORT).show();
    }

}
