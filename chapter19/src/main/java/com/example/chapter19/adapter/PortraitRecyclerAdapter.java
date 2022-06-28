package com.example.chapter19.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.chapter19.R;
import com.example.chapter19.entity.PersonPortrait;

import java.util.List;

public class PortraitRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final static String TAG = "PortraitRecyclerAdapter";
    private Context mContext; // 声明一个上下文对象
    private List<PersonPortrait> mPortraitList; // 人员头像列表
    private OnItemClickListener mListener; // 点击监听器

    public PortraitRecyclerAdapter(Context context, List<PersonPortrait> portraitList) {
        mContext = context;
        mPortraitList = portraitList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    // 获取列表项的个数
    @Override
    public int getItemCount() {
        return mPortraitList.size();
    }

    // 创建列表项的视图持有者
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup vg, int viewType) {
        // 根据布局文件item_portrait.xml生成视图对象
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_portrait, vg, false);
        return new ItemHolder(v);
    }

    // 绑定列表项的视图持有者
    @Override
    public void onBindViewHolder(ViewHolder vh, final int position) {
        ItemHolder holder = (ItemHolder) vh;
        PersonPortrait portrait = mPortraitList.get(position);
        if (portrait.getType() == -1) {
            holder.iv_portrait.setImageResource(R.drawable.add_pic);
            holder.iv_portrait.setOnClickListener(v -> mListener.onItemClick(position));
        } else {
            // 设置图像视图的路径对象
            holder.iv_portrait.setImageURI(Uri.parse(mPortraitList.get(position).getPath()));
        }
        if (portrait.getType() == 0) {
            holder.tv_similarity.setVisibility(View.GONE);
        } else if (portrait.getType() == 1) {
            holder.tv_similarity.setVisibility(View.VISIBLE);
            String desc = String.format("相似度：%.02f", portrait.getSimilarity());
            holder.tv_similarity.setText(desc);
        }
    }

    // 获取列表项的类型
    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    // 获取列表项的编号
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 定义列表项的视图持有者
    public class ItemHolder extends ViewHolder {
        public ImageView iv_portrait; // 声明一个图像视图对象
        public TextView tv_similarity; // 声明一个文本视图对象

        public ItemHolder(View v) {
            super(v);
            iv_portrait = v.findViewById(R.id.iv_portrait);
            tv_similarity = v.findViewById(R.id.tv_similarity);
        }
    }

    // 定义一个循环视图列表项的点击监听器接口
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
