package com.example.chapter20.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chapter20.R;
import com.example.chapter20.bean.EntityInfo;
import com.example.chapter20.util.ChatUtil;

import java.util.List;

public class EntityListAdapter extends BaseAdapter {
    private Context mContext; // 声明一个上下文对象
    private List<EntityInfo> mUserList; // 声明一个用户信息列表

    // 用户适配器的构造方法，传入上下文与用户列表
    public EntityListAdapter(Context context, List<EntityInfo> user_list) {
        mContext = context;
        mUserList = user_list;
    }

    // 获取列表项的个数
    @Override
    public int getCount() {
        return mUserList.size();
    }

    // 获取列表项的数据
    @Override
    public Object getItem(int arg0) {
        return mUserList.get(arg0);
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
            // 根据布局文件item_user.xml生成转换视图对象
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_user, null);
            holder.iv_portrait = convertView.findViewById(R.id.iv_portrait);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_relation = convertView.findViewById(R.id.tv_relation);
            convertView.setTag(holder); // 将视图持有者保存到转换视图当中
        } else { // 转换视图非空
            // 从转换视图中获取之前保存的视图持有者
            holder = (ViewHolder) convertView.getTag();
        }
        EntityInfo user = mUserList.get(position);
        holder.tv_name.setText(user.name); // 显示用户的名称
        holder.tv_relation.setText(user.relation); // 显示用户的描述
        Drawable drawable = ChatUtil.getPortraitByName(mContext, user.name); // 获取用户的头像
        holder.iv_portrait.setImageDrawable(drawable); // 显示用户的头像
        return convertView;
    }

    // 定义一个视图持有者，以便重用列表项的视图资源
    public final class ViewHolder {
        public ImageView iv_portrait; // 声明用户头像的图像视图对象
        public TextView tv_name; // 声明用户名称的文本视图对象
        public TextView tv_relation; // 声明用户关系的文本视图对象
    }

}
