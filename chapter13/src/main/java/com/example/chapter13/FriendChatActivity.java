package com.example.chapter13;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter13.bean.ImageMessage;
import com.example.chapter13.bean.ImagePart;
import com.example.chapter13.bean.MessageInfo;
import com.example.chapter13.util.BitmapUtil;
import com.example.chapter13.util.ChatUtil;
import com.example.chapter13.util.DateUtil;
import com.example.chapter13.util.SocketUtil;
import com.example.chapter13.util.Utils;
import com.example.chapter13.util.ViewUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import io.socket.client.Socket;

public class FriendChatActivity extends AppCompatActivity {
    private static final String TAG = "FriendChatActivity";
    private EditText et_input; // 声明一个编辑框对象
    private ScrollView sv_chat; // 声明一个滚动视图对象
    private LinearLayout ll_show; // 声明一个聊天窗口的线性布局对象
    private int dip_margin; // 每条聊天记录的四周空白距离
    private int CHOOSE_CODE = 3; // 只在相册挑选图片的请求码

    private String mSelfName, mFriendName; // 自己名称，好友名称
    private Socket mSocket; // 声明一个套接字对象
    private String mMinute = "00:00"; // 时间提示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_chat);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕常亮
        mSelfName = getIntent().getStringExtra("self_name");
        mFriendName = getIntent().getStringExtra("friend_name");
        initView(); // 初始化视图
        initSocket(); // 初始化套接字
    }

    // 初始化视图
    private void initView() {
        dip_margin = Utils.dip2px(this, 5);
        TextView tv_title = findViewById(R.id.tv_title);
        et_input = findViewById(R.id.et_input);
        sv_chat = findViewById(R.id.sv_chat);
        ll_show = findViewById(R.id.ll_show);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        findViewById(R.id.ib_img).setOnClickListener(v -> {
            // 创建一个内容获取动作的意图（准备跳到系统相册）
            Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
            albumIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false); // 是否允许多选
            albumIntent.setType("image/*"); // 类型为图像
            startActivityForResult(albumIntent, CHOOSE_CODE); // 打开系统相册
        });
        findViewById(R.id.btn_send).setOnClickListener(v -> sendMessage());
        tv_title.setText(mFriendName);
    }

    // 初始化套接字
    private void initSocket() {
        mSocket = MainApplication.getInstance().getSocket();
        // 等待接收好友消息
        mSocket.on("receive_friend_message", (args) -> {
            JSONObject json = (JSONObject) args[0];
            Log.d(TAG, "receive_friend_message:"+json.toString());
            MessageInfo message = new Gson().fromJson(json.toString(), MessageInfo.class);
            // 往聊天窗口添加文本消息
            runOnUiThread(() -> appendChatMsg(message.from, message.content, false));
        });
        // 等待接收好友图片
        mSocket.on("receive_friend_image", (args) -> receiveImage(args));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("receive_friend_message"); // 取消接收好友消息
        mSocket.off("receive_friend_image"); // 取消接收好友图片
    }

    // 发送聊天消息
    private void sendMessage() {
        String content = et_input.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "请输入聊天消息", Toast.LENGTH_SHORT).show();
            return;
        }
        et_input.setText("");
        ViewUtil.hideOneInputMethod(this, et_input); // 隐藏软键盘
        appendChatMsg(mSelfName, content, true); // 往聊天窗口添加文本消息
        // 下面向Socket服务器发送聊天消息
        MessageInfo message = new MessageInfo(mSelfName, mFriendName, content);
        SocketUtil.emit(mSocket, "send_friend_message", message);
    }

    // 往聊天窗口添加聊天消息
    private void appendChatMsg(String name, String content, boolean isSelf) {
        appendNowMinute(); // 往聊天窗口添加当前时间
        // 把单条消息的线性布局添加到聊天窗口上
        ll_show.addView(ChatUtil.getChatView(this, name, content, isSelf));
        // 延迟100毫秒后启动聊天窗口的滚动任务
        new Handler(Looper.myLooper()).postDelayed(() -> {
            sv_chat.fullScroll(ScrollView.FOCUS_DOWN); // 滚动到底部
        }, 100);
    }

    // 往聊天窗口添加当前时间
    private void appendNowMinute() {
        String nowMinute = DateUtil.getNowMinute();
        // 分钟数切换时才需要添加当前时间
        if (!mMinute.substring(0, 4).equals(nowMinute.substring(0, 4))) {
            mMinute = nowMinute;
            ll_show.addView(ChatUtil.getHintView(this, nowMinute, dip_margin));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == CHOOSE_CODE) { // 从相册返回
            if (intent.getData() != null) { // 从相册选择一张照片
                Uri uri = intent.getData(); // 获得已选择照片的路径对象
                String path = uri.toString();
                String imageName = path.substring(path.lastIndexOf("/")+1);
                // 根据指定图片的uri，获得自动缩小后的图片路径
                String imagePath = BitmapUtil.getAutoZoomPath(this, uri);
                // 往聊天窗口添加图片消息
                appendChatImage(mSelfName, imagePath, true);
                sendImage(imageName, imagePath); // 分段传输图片数据
            }
        }
    }

    private int mBlock = 50*1024; // 每段的数据包大小
    // 分段传输图片数据
    private void sendImage(String imageName, String imagePath) {
        Log.d(TAG, "sendImage");
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把位图数据压缩到字节数组输出流
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] bytes = baos.toByteArray();
        int count = bytes.length/mBlock + 1;
        Log.d(TAG, "sendImage length="+bytes.length+", count="+count);
        // 下面把图片数据经过BASE64编码后发给Socket服务器
        for (int i=0; i<count; i++) {
            Log.d(TAG, "sendImage i="+i);
            String encodeData = "";
            if (i == count-1) {
                int remain = bytes.length % mBlock;
                byte[] temp = new byte[remain];
                System.arraycopy(bytes, i*mBlock, temp, 0, remain);
                encodeData = Base64.encodeToString(temp, Base64.DEFAULT);
            } else {
                byte[] temp = new byte[mBlock];
                System.arraycopy(bytes, i*mBlock, temp, 0, mBlock);
                encodeData = Base64.encodeToString(temp, Base64.DEFAULT);
            }
            // 往Socket服务器发送本段的图片数据
            ImagePart part = new ImagePart(imageName, encodeData, i, bytes.length);
            ImageMessage message = new ImageMessage(mSelfName, mFriendName, part);
            SocketUtil.emit(mSocket, "send_friend_image", message);
        }
    }

    private String mLastFile; // 上次的文件名
    private int mReceiveCount; // 接收包的数量
    private byte[] mReceiveData; // 收到的字节数组
    // 接收对方传来的图片数据
    private void receiveImage(Object... args) {
        JSONObject json = (JSONObject) args[0];
        ImageMessage message = new Gson().fromJson(json.toString(), ImageMessage.class);
        ImagePart part = message.getPart();
        if (!part.getName().equals(mLastFile)) { // 与上次文件名不同，表示开始接收新文件
            mLastFile = part.getName();
            mReceiveCount = 0;
            mReceiveData = new byte[part.getLength()];
        }
        mReceiveCount++;
        // 把接收到的图片数据通过BASE64解码为字节数组
        byte[] temp = Base64.decode(part.getData(), Base64.DEFAULT);
        System.arraycopy(temp, 0, mReceiveData, part.getSeq()*mBlock, temp.length);
        // 所有数据包都接收完毕
        if (mReceiveCount >= part.getLength()/mBlock+1) {
            // 从字节数组中解码得到位图对象
            Bitmap bitmap = BitmapFactory.decodeByteArray(mReceiveData, 0, mReceiveData.length);
            String imagePath = String.format("%s/%s.jpg",
                    getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),
                    DateUtil.getNowDateTime());
            BitmapUtil.saveImage(imagePath, bitmap);
            // 往聊天窗口添加图片消息
            runOnUiThread(() -> appendChatImage(message.getFrom(), imagePath, false));
        }
    }

    // 往聊天窗口添加图片消息
    private void appendChatImage(String name, String imagePath, boolean isSelf) {
        appendNowMinute(); // 往聊天窗口添加当前时间
        // 把图片消息的线性布局添加到聊天窗口上
        ll_show.addView(ChatUtil.getChatImage(this, name, imagePath, isSelf));
        // 延迟100毫秒后启动聊天窗口的滚动任务
        new Handler(Looper.myLooper()).postDelayed(() -> {
            sv_chat.fullScroll(ScrollView.FOCUS_DOWN); // 滚动到底部
        }, 100);
    }

}