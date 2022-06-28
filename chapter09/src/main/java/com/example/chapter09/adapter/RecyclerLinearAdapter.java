package com.example.chapter09.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.chapter09.R;
import com.example.chapter09.bean.NewsInfo;

import java.util.List;

public class RecyclerLinearAdapter extends RecyclerView.Adapter<ViewHolder> {
    private final static String TAG = "RecyclerLinearAdapter";
    private Context mContext; // 声明一个上下文对象
    private List<NewsInfo> mPublicList; // 公众号列表

    public RecyclerLinearAdapter(Context context, List<NewsInfo> publicList) {
        mContext = context;
        mPublicList = publicList;
    }

    // 获取列表项的个数
    public int getItemCount() {
        return mPublicList.size();
    }

    // 创建列表项的视图持有者
    public ViewHolder onCreateViewHolder(ViewGroup vg, int viewType) {
        // 根据布局文件item_linear.xml生成视图对象
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_linear, vg, false);
        return new ItemHolder(v);
    }

    // 绑定列表项的视图持有者
    public void onBindViewHolder(ViewHolder vh, final int position) {
        ItemHolder holder = (ItemHolder) vh;
        holder.iv_pic.setImageResource(mPublicList.get(position).pic_id);
        holder.tv_title.setText(mPublicList.get(position).title);
        holder.tv_desc.setText(mPublicList.get(position).desc);
    }

//    // 获取列表项的类型，这里的类型与onCreateViewHolder方法的viewType参数保持一致
//    public int getItemViewType(int position) {
//        return 0;
//    }
//
//    // 获取列表项的编号
//    public long getItemId(int position) {
//        return position;
//    }

    // 定义列表项的视图持有者
    public class ItemHolder extends RecyclerView.ViewHolder {
        public ImageView iv_pic; // 声明列表项图标的图像视图
        public TextView tv_title; // 声明列表项标题的文本视图
        public TextView tv_desc; // 声明列表项描述的文本视图

        public ItemHolder(View v) {
            super(v);
            iv_pic = v.findViewById(R.id.iv_pic);
            tv_title = v.findViewById(R.id.tv_title);
            tv_desc = v.findViewById(R.id.tv_desc);
        }
    }

}
