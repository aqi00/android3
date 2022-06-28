package com.example.chapter13.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.chapter13.GroupChatActivity;
import com.example.chapter13.MainApplication;
import com.example.chapter13.R;
import com.example.chapter13.adapter.EntityListAdapter;
import com.example.chapter13.bean.EntityInfo;
import com.example.chapter13.widget.NoScrollListView;

import java.util.ArrayList;
import java.util.List;

public class GroupListFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "GroupListFragment";
    protected View mView; // 声明一个视图对象
    protected Context mContext; // 声明一个上下文对象
    private NoScrollListView nslv_group; // 声明一个不滚动视图对象
    private List<EntityInfo> mGroupList = new ArrayList<>(); // 群组列表

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 简单起见固定写几个群组，读者可尝试改造为动态创建群组
        mGroupList.add(new EntityInfo("Android开发技术交流群", ""));
        mGroupList.add(new EntityInfo("摄影爱好者", ""));
        mGroupList.add(new EntityInfo("人工智能学习讨论群", ""));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity(); // 获取活动页面的上下文
        mView = inflater.inflate(R.layout.fragment_group_list, container, false);
        TextView tv_title = mView.findViewById(R.id.tv_title);
        tv_title.setText(String.format("群聊（%d）", mGroupList.size()));
        mView.findViewById(R.id.iv_back).setOnClickListener(v -> getActivity().finish());
        nslv_group = mView.findViewById(R.id.nslv_group);
        EntityListAdapter adapter = new EntityListAdapter(mContext, mGroupList);
        nslv_group.setAdapter(adapter);
        nslv_group.setOnItemClickListener(this);
        return mView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EntityInfo group = mGroupList.get(position);
        // 以下跳到在指定群组聊天的界面
        Intent intent = new Intent(mContext, GroupChatActivity.class);
        intent.putExtra("self_name", MainApplication.getInstance().wechatName);
        intent.putExtra("group_name", group.name);
        startActivity(intent);
    }

}
