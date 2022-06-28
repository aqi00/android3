package com.example.chapter18.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chapter18.R;
import com.example.chapter18.bean.WordInfo;

import java.util.List;

public class WordRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = "WordRecyclerAdapter";
    private Context mContext; // 声明一个上下文对象
    private List<WordInfo> mWordList; // 单词列表

    // 单词适配器的构造方法，传入上下文与单词列表
    public WordRecyclerAdapter(Context context, List<WordInfo> wordList) {
        mContext = context;
        mWordList = wordList;
    }

    // 获取列表项的个数
    @Override
    public int getItemCount() {
        return mWordList.size();
    }

    // 创建列表项的视图持有者
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup vg, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_word, vg, false);
        return new ItemHolder(v);
    }

    // 绑定列表项的视图持有者
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position) {
        ItemHolder holder = (ItemHolder) vh;
        WordInfo word = mWordList.get(position);
        holder.tv_word.setText(word.name);
        holder.tv_percent.setText(word.percent);
        if (TextUtils.isEmpty(word.percent)) { // 百分比为空，则正常显示
            holder.ll_word.setBackgroundColor(Color.WHITE);
            holder.tv_word.setTextColor(Color.BLACK);
            holder.tv_percent.setVisibility(View.GONE);
        } else { // 百分比非空，则高亮显示
            holder.ll_word.setBackgroundColor(Color.CYAN);
            holder.tv_word.setTextColor(Color.RED);
            holder.tv_percent.setVisibility(View.VISIBLE);
        }
    }

    // 定义列表项的视图持有者
    public class ItemHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll_word;
        public TextView tv_word;
        public TextView tv_percent;

        public ItemHolder(View v) {
            super(v);
            ll_word = v.findViewById(R.id.ll_word);
            tv_word = v.findViewById(R.id.tv_word);
            tv_percent = v.findViewById(R.id.tv_percent);
        }
    }

}
