package com.example.chapter20;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter20.adapter.EntityListAdapter;
import com.example.chapter20.bean.EntityInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;

public class ContactListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final static String TAG = "ContactListActivity";
    private EntityListAdapter mAdapter; // 联系人的列表适配器
    private Map<String, EntityInfo> mContactMap = new HashMap<>(); // 联系人的名称映射
    private List<EntityInfo> mContactList = new ArrayList<>(); // 联系人列表
    private Socket mSocket; // 声明一个套接字对象
    private String mSelfName; // 我的昵称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initView(); // 初始化视图
        initSocket(); // 初始化套接字
    }

    // 初始化视图
    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("联系人列表");
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        ListView lv_contact = findViewById(R.id.lv_contact);
        mAdapter = new EntityListAdapter(this, mContactList);
        lv_contact.setAdapter(mAdapter);
        lv_contact.setOnItemClickListener(this);
    }

    // 初始化套接字
    private void initSocket() {
        mSelfName = getIntent().getStringExtra("self_name");
        Log.d(TAG , "initSocket "+mSelfName);
        mSocket = MainApplication.getInstance().getSocket();
        mSocket.connect(); // 建立Socket连接
        // 开始监听好友上线事件
        mSocket.on("friend_online", (args) -> {
            String friend_name = (String) args[0];
            if (friend_name != null) {
                // 把刚上线的好友加入联系人列表
                mContactMap.put(friend_name, new EntityInfo(friend_name, "好友"));
                mContactList.clear();
                mContactList.addAll(mContactMap.values());
                runOnUiThread(() -> mAdapter.notifyDataSetChanged());
            }
        });
        // 开始监听好友下线事件
        mSocket.on("friend_offline", (args) -> {
            String friend_name = (String) args[0];
            if (friend_name != null) {
                mContactMap.remove(friend_name); // 从联系人列表移除已下线的好友
                mContactList.clear();
                mContactList.addAll(mContactMap.values());
                runOnUiThread(() -> mAdapter.notifyDataSetChanged());
            }
        });
        // 开始监听好友通话事件
        mSocket.on("friend_converse", (args) -> {
            String friend_name = (String) args[0];
            // 接收到好友的通话请求，于是跳到视频通话页面
            Intent intent = new Intent(this, ContactVideoActivity.class);
            intent.putExtra("self_name", mSelfName); // 我的昵称
            intent.putExtra("friend_name", friend_name); // 好友昵称
            intent.putExtra("is_offer", false); // 是否为发起方
            startActivity(intent);
        });
        mSocket.emit("self_online", mSelfName); // 通知服务器“我已上线”
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket.connected()) { // 已经连上Socket服务器
            mSocket.emit("self_offline", mSelfName); // 通知服务器“我已下线”
            mSocket.off("friend_online"); // 取消监听好友上线事件
            mSocket.off("friend_offline"); // 取消监听好友下线事件
            mSocket.off("friend_converse"); // 取消监听好友通话事件
            mSocket.disconnect(); // 断开Socket连接
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EntityInfo friend = mContactList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format("你是否要跟%s视频通话？", friend.name));
        builder.setPositiveButton("是", (dialog, which) -> {
            // 想跟好友通话，就打开视频通话页面
            Intent intent = new Intent(this, ContactVideoActivity.class);
            intent.putExtra("self_name", mSelfName); // 我的昵称
            intent.putExtra("friend_name", friend.name); // 好友昵称
            intent.putExtra("is_offer", true); // 是否为发起方
            startActivity(intent);
        });
        builder.setNegativeButton("否", null);
        builder.create().show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(this, "视频通话已结束", Toast.LENGTH_SHORT).show();
    }
}