package com.example.chapter09.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.chapter09.R;

public class DepartmentCartFragment extends Fragment {
    protected View mView; // 声明一个视图对象
    protected AppCompatActivity mActivity; // 声明一个活动对象

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (AppCompatActivity) getActivity();
        mView = inflater.inflate(R.layout.fragment_department_cart, container, false);
        // 从布局文件中获取名叫tl_head的工具栏
        Toolbar tl_head = mView.findViewById(R.id.tl_head);
        tl_head.setTitle("购物车"); // 设置工具栏的标题文字
        mActivity.setSupportActionBar(tl_head); // 使用tl_head替换系统自带的ActionBar
        return mView;
    }
}
