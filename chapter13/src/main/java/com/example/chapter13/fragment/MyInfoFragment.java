package com.example.chapter13.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.chapter13.MainApplication;
import com.example.chapter13.R;
import com.example.chapter13.util.ChatUtil;
import com.example.chapter13.widget.InputDialog;

import io.socket.client.Socket;

public class MyInfoFragment extends Fragment {
    private static final String TAG = "MyInfoFragment";
    protected View mView; // 声明一个视图对象
    protected Context mContext; // 声明一个上下文对象
    private ImageView iv_portrait; // 声明一个图像视图对象
    private TextView tv_nick; // 声明一个文本视图对象
    private Socket mSocket; // 声明一个套接字对象

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity(); // 获取活动页面的上下文
        mView = inflater.inflate(R.layout.fragment_my_info, container, false);
        initView(); // 初始化视图
        showPortrait(); // 显示用户昵称和用户头像
        mSocket = MainApplication.getInstance().getSocket();
        return mView;
    }

    // 初始化视图
    private void initView() {
        TextView tv_title = mView.findViewById(R.id.tv_title);
        tv_title.setText("个人信息");
        mView.findViewById(R.id.iv_back).setOnClickListener(v -> getActivity().finish());
        iv_portrait = mView.findViewById(R.id.iv_portrait);
        tv_nick = mView.findViewById(R.id.tv_nick);
        mView.findViewById(R.id.ll_nick).setOnClickListener(v -> modifyNickName());
    }

    // 显示用户昵称和用户头像
    private void showPortrait() {
        String nickName = MainApplication.getInstance().wechatName;
        tv_nick.setText(nickName);
        Drawable drawable = ChatUtil.getPortraitByName(mContext, nickName);
        iv_portrait.setImageDrawable(drawable);
    }

    // 修改用户昵称
    private void modifyNickName() {
        String nickName = MainApplication.getInstance().wechatName;
        // 弹出昵称填写对话框
        InputDialog didialog = new InputDialog(mContext, nickName, 0,
                "请输入新的昵称", (idt, content, seq) -> {
            MainApplication.getInstance().wechatName = content;
            showPortrait(); // 显示用户昵称和用户头像
            // 旧昵称下线，新昵称上线，从而完成改名操作
            mSocket.emit("self_offline", nickName);
            mSocket.emit("self_online", MainApplication.getInstance().wechatName);
        });
        didialog.show();
    }

}
