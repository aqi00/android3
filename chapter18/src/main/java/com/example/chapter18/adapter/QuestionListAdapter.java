package com.example.chapter18.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.chapter18.R;
import com.example.chapter18.entity.QuestionInfo;

import java.util.List;

public class QuestionListAdapter extends BaseAdapter {
    private Context mContext; // 声明一个上下文对象
    private List<QuestionInfo> mQuestionList; // 声明一个问答信息列表

    // 问答适配器的构造方法，传入上下文与问答列表
    public QuestionListAdapter(Context context, List<QuestionInfo> questionList) {
        mContext = context;
        mQuestionList = questionList;
    }

    // 获取列表项的个数
    @Override
    public int getCount() {
        return mQuestionList.size();
    }

    // 获取列表项的数据
    @Override
    public Object getItem(int arg0) {
        return mQuestionList.get(arg0);
    }

    // 获取列表项的编号
    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    // 获取指定位置的列表项视图
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) { // 转换视图为空
            holder = new ViewHolder(); // 创建一个新的视图持有者
            // 根据布局文件item_question.xml生成转换视图对象
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_question, null);
            holder.tv_question = convertView.findViewById(R.id.tv_question);
            convertView.setTag(holder); // 将视图持有者保存到转换视图当中
        } else { // 转换视图非空
            // 从转换视图中获取之前保存的视图持有者
            holder = (ViewHolder) convertView.getTag();
        }
        QuestionInfo questionInfo = mQuestionList.get(position);
        holder.tv_question.setText(questionInfo.getQuestion()); // 显示问题的名称
        return convertView;
    }

    // 定义一个视图持有者，以便重用列表项的视图资源
    public final class ViewHolder {
        public TextView tv_question; // 声明问题名称的文本视图对象
    }
}
