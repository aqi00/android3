package com.example.chapter09.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.chapter09.R;

public class MobileFragment extends Fragment {
    private static final String TAG = "MobileFragment";
    protected View mView; // 声明一个视图对象
    protected Context mContext; // 声明一个上下文对象
    private int mPosition; // 位置序号
    private int mImageId; // 图片的资源编号
    private String mDesc; // 商品的文字描述

    // 获取该碎片的一个实例
    public static MobileFragment newInstance(int position, int image_id, String desc) {
        MobileFragment fragment = new MobileFragment(); // 创建该碎片的一个实例
        Bundle bundle = new Bundle(); // 创建一个新包裹
        bundle.putInt("position", position); // 往包裹存入位置序号
        bundle.putInt("image_id", image_id); // 往包裹存入图片的资源编号
        bundle.putString("desc", desc); // 往包裹存入商品的文字描述
        fragment.setArguments(bundle); // 把包裹塞给碎片
        return fragment; // 返回碎片实例
    }

    // 创建碎片视图
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity(); // 获取活动页面的上下文
        if (getArguments() != null) { // 如果碎片携带有包裹，就打开包裹获取参数信息
            mPosition = getArguments().getInt("position", 0);
            mImageId = getArguments().getInt("image_id", 0);
            mDesc = getArguments().getString("desc");
        }
        // 根据布局文件item_mobile.xml生成视图对象
        mView = inflater.inflate(R.layout.item_mobile, container, false);
        ImageView iv_pic = mView.findViewById(R.id.iv_pic);
        TextView tv_desc = mView.findViewById(R.id.tv_desc);
        iv_pic.setImageResource(mImageId);
        tv_desc.setText(mDesc);
        Log.d(TAG, "onCreateView position=" + mPosition);
        return mView; // 返回该碎片的视图对象
    }

}
