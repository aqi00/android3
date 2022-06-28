package com.example.chapter19.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chapter19.R;
import com.example.chapter19.entity.PersonInfo;

import java.util.List;

public class PersonListAdapter extends BaseAdapter {
    private List<PersonInfo> mPersonList; // 声明一个人员信息列表
    private Context mContext; // 声明一个上下文对象

    public PersonListAdapter(Context context, List<PersonInfo> personList) {
        mContext = context;
        mPersonList = personList;
    }

    @Override
    public int getCount() {
        return mPersonList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mPersonList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_person, null);
            holder.iv_portrait = convertView.findViewById(R.id.iv_portrait);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_status = convertView.findViewById(R.id.tv_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PersonInfo person = mPersonList.get(position);
        Uri uri = Uri.parse(person.getPortraitList().get(0).getPath());
        holder.iv_portrait.setImageURI(uri); // 设置图像视图的路径对象
        holder.tv_name.setText(person.getName());
        holder.tv_status.setText((person.getFlag()==0) ? "待识别" : "已识别");
        return convertView;
    }

    public final class ViewHolder {
        public ImageView iv_portrait;
        public TextView tv_name;
        public TextView tv_status;
    }

}
