package com.example.chapter13;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.chapter13.adapter.WeChatAdapter;

import io.socket.client.Socket;

public class WeChatActivity extends AppCompatActivity {
    private ViewPager vp_content; // 声明一个翻页视图对象
    private RadioGroup rg_tabbar; // 声明一个单选组对象
    private Socket mSocket; // 声明一个套接字对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_chat);
        initView(); // 初始化视图
        mSocket = MainApplication.getInstance().getSocket();
        mSocket.connect(); // 建立Socket连接
    }

    // 初始化视图
    private void initView() {
        vp_content = findViewById(R.id.vp_content);
        // 构建一个翻页适配器
        WeChatAdapter adapter = new WeChatAdapter(getSupportFragmentManager());
        vp_content.setAdapter(adapter); // 设置翻页视图的适配器
        // 给翻页视图添加页面变更监听器
        vp_content.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // 选中指定位置的单选按钮
                rg_tabbar.check(rg_tabbar.getChildAt(position).getId());
            }
        });
        rg_tabbar = findViewById(R.id.rg_tabbar);
        // 设置单选组的选中监听器
        rg_tabbar.setOnCheckedChangeListener((group, checkedId) -> {
            for (int pos=0; pos<rg_tabbar.getChildCount(); pos++) {
                // 获得指定位置的单选按钮
                RadioButton tab = (RadioButton) rg_tabbar.getChildAt(pos);
                if (tab.getId() == checkedId) { // 正是当前选中的按钮
                    vp_content.setCurrentItem(pos); // 设置翻页视图显示第几页
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket.connected()) { // 已经连上Socket服务器
            mSocket.disconnect(); // 断开Socket连接
        }
    }

}