package com.example.chapter14.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.chapter14.R;

import java.util.List;

public class CoverRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final static String TAG = "CoverRecyclerAdapter";
    private Context mContext; // 声明一个上下文对象
    private List<String> mPathList; // 封面图片的路径列表
    private OnItemClickListener mListener; // 点击监听器
    private int mSelectedPos = 0; // 选中的图片序号

    public CoverRecyclerAdapter(Context context, int selectedPos, List<String> pathList, OnItemClickListener listener) {
        mContext = context;
        mSelectedPos = selectedPos;
        mPathList = pathList;
        mListener = listener;
    }

    // 获取列表项的个数
    @Override
    public int getItemCount() {
        return mPathList.size();
    }

    // 创建列表项的视图持有者
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup vg, int viewType) {
        // 根据布局文件item_cover.xml生成视图对象
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_cover, vg, false);
        return new ItemHolder(v);
    }

    // 绑定列表项的视图持有者
    @Override
    public void onBindViewHolder(ViewHolder vh, final int position) {
        Log.d(TAG, "position="+position+", mSelectedPos="+mSelectedPos);
        ItemHolder holder = (ItemHolder) vh;
        // 设置图像视图的路径对象
        holder.iv_cover.setImageURI(Uri.parse(mPathList.get(position)));
        holder.rl_cover.setOnClickListener(v -> {
            mListener.onItemClick(position);
            notifyItemChanged(mSelectedPos); // 通知该位置的列表项发生变更
            mSelectedPos = position;
            notifyItemChanged(mSelectedPos); // 通知该位置的列表项发生变更
        });
        if (position == mSelectedPos) { // 被选中的图片添加高亮红框
            holder.v_box.setVisibility(View.VISIBLE);
        } else { // 未选中的图片取消高亮红框
            holder.v_box.setVisibility(View.GONE);
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
        public RelativeLayout rl_cover; // 声明一个相对布局对象
        public ImageView iv_cover; // 声明一个图像视图对象
        public View v_box; // 声明一个视图对象

        public ItemHolder(View v) {
            super(v);
            rl_cover = v.findViewById(R.id.rl_cover);
            iv_cover = v.findViewById(R.id.iv_cover);
            v_box = v.findViewById(R.id.v_box);
        }
    }

    // 定义一个循环视图列表项的点击监听器接口
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
