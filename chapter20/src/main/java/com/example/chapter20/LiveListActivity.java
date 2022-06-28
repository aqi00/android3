package com.example.chapter20;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.chapter20.adapter.EntityListAdapter;
import com.example.chapter20.bean.EntityInfo;
import com.example.chapter20.bean.RoomInfo;
import com.example.chapter20.bean.RoomSet;
import com.example.chapter20.util.SocketUtil;
import com.example.chapter20.widget.InputDialog;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;

public class LiveListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final static String TAG = "LiveListActivity";
    private String mSelfName; // 我的昵称
    private EntityListAdapter mAdapter; // 直播房间的列表适配器
    private Map<String, EntityInfo> mRoomMap = new HashMap<>(); // 直播房间的名称映射
    private List<EntityInfo> mRoomList = new ArrayList<>(); // 直播房间列表
    private Socket mSocket; // 声明一个套接字对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_list);
        mSelfName = getIntent().getStringExtra("self_name");
        initView(); // 初始化视图
        initSocket(); // 初始化套接字
    }

    // 初始化视图
    private void initView() {
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("直播房间列表");
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        mAdapter = new EntityListAdapter(this, mRoomList);
        ListView lv_room = findViewById(R.id.lv_room);
        lv_room.setAdapter(mAdapter);
        lv_room.setOnItemClickListener(this);
        findViewById(R.id.btn_open_room).setOnClickListener(v -> openCreateDialog());
    }

    // 初始化套接字
    private void initSocket() {
        mSocket = MainApplication.getInstance().getSocket();
        mSocket.connect(); // 建立Socket连接
        // 等待服务器返回直播房间列表
        mSocket.on("return_room_list", (args) -> {
            JSONObject json = (JSONObject) args[0];
            Log.d(TAG, "return_room_list: "+json.toString());
            RoomSet roomSet = new Gson().fromJson(json.toString(), RoomSet.class);
            if (roomSet!=null && roomSet.getRoom_list()!=null) {
                Log.d(TAG, "getRoom_list().size: "+roomSet.getRoom_list().size());
                mRoomMap.clear();
                for (RoomInfo room : roomSet.getRoom_list()) {
                    mRoomMap.put(room.getRoom_name(), new EntityInfo(room.getRoom_name(), "主播："+room.getAnchor_name(), room));
                }
                mRoomList.clear();
                mRoomList.addAll(mRoomMap.values());
                runOnUiThread(() -> mAdapter.notifyDataSetChanged());
            }
        });
        // 等待新房间的开通事件
        mSocket.on("room_have_opened", (args) -> {
            JSONObject json = (JSONObject) args[0];
            Log.d(TAG, "room_have_opened: "+json.toString());
            RoomInfo room = new Gson().fromJson(json.toString(), RoomInfo.class);
            mRoomMap.put(room.getRoom_name(), new EntityInfo(room.getRoom_name(), "主播："+room.getAnchor_name(), room));
            mRoomList.clear();
            mRoomList.addAll(mRoomMap.values());
            runOnUiThread(() -> mAdapter.notifyDataSetChanged());
        });
        // 等待原房间的关闭事件
        mSocket.on("room_have_closed", (args) -> {
            String roomName = (String) args[0];
            mRoomMap.remove(roomName);
            mRoomList.clear();
            mRoomList.addAll(mRoomMap.values());
            runOnUiThread(() -> mAdapter.notifyDataSetChanged());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 向服务器请求获取直播房间列表
        new Handler(Looper.myLooper()).postDelayed(() -> mSocket.emit("get_room_list", mSelfName), 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("return_room_list"); // 取消监听房间列表事件
        mSocket.off("room_have_opened"); // 取消监听房间开通事件
        mSocket.off("room_have_closed"); // 取消监听房间关闭事件
        if (mSocket.connected()) { // 已经连上Socket服务器
            mSocket.disconnect(); // 断开Socket连接
        }
    }

    // 打开房间创建对话框
    private void openCreateDialog() {
        InputDialog didialog = new InputDialog(this, "", 0,
                "请输入直播间名称", (idt, content, seq) -> {
            String roomName = content;
            RoomInfo room = new RoomInfo(mSelfName, roomName, new HashMap<>());
            SocketUtil.emit(mSocket, "open_room", room); // 发送房间开通事件
            // 主动开通房间，就跳到主播自己的直播页面
            Intent intent = new Intent(this, LiveServerActivity.class);
            intent.putExtra("self_name", mSelfName);
            intent.putExtra("room_name", roomName);
            startActivity(intent);
        });
        didialog.show(); // 弹出创建房间对话框
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        EntityInfo room = mRoomList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(String.format("你是否要进入%s？", room.name));
        builder.setPositiveButton("是", (dialog, which) -> {
            // 想要进入某个房间，就跳到用户自己的直播页面
            Intent intent = new Intent(this, LiveClientActivity.class);
            intent.putExtra("self_name", mSelfName);
            intent.putExtra("room_name", room.name);
            intent.putExtra("anchor_name", ((RoomInfo) room.info).getAnchor_name());
            startActivity(intent);
        });
        builder.setNegativeButton("否", null);
        builder.create().show();
    }
}