package com.example.chapter13;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter13.bean.ImagePart;
import com.example.chapter13.constant.NetConst;
import com.example.chapter13.util.BitmapUtil;
import com.example.chapter13.util.DateUtil;
import com.example.chapter13.util.SocketUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketioImageActivity extends AppCompatActivity {
    private static final String TAG = "SocketioImageActivity";
    private ImageView iv_request; // 声明一个图像视图对象
    private ImageView iv_response; // 声明一个图像视图对象
    private TextView tv_response; // 声明一个文本视图对象
    private int CHOOSE_CODE = 3; // 只在相册挑选图片的请求码
    private String mFileName; // 图片名称
    private Bitmap mBitmap; // 位图对象
    private Socket mSocket; // 声明一个套接字对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socketio_image);
        iv_request = findViewById(R.id.iv_request);
        iv_response = findViewById(R.id.iv_response);
        tv_response = findViewById(R.id.tv_response);
        findViewById(R.id.btn_choose).setOnClickListener(v -> {
            // 创建一个内容获取动作的意图（准备跳到系统相册）
            Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
            albumIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false); // 是否允许多选
            albumIntent.setType("image/*"); // 类型为图像
            startActivityForResult(albumIntent, CHOOSE_CODE); // 打开系统相册
        });
        findViewById(R.id.btn_send).setOnClickListener(v -> {
            if (mBitmap == null) {
                Toast.makeText(this, "请先选择图片文件", Toast.LENGTH_SHORT).show();
                return;
            }
            sendImage(); // 分段传输图片数据
        });
        initSocket(); // 初始化套接字
    }

    // 初始化套接字
    private void initSocket() {
        // 检查能否连上Socket服务器
        SocketUtil.checkSocketAvailable(this, NetConst.BASE_IP, NetConst.BASE_PORT);
        try {
            String uri = String.format("http://%s:%d/", NetConst.BASE_IP, NetConst.BASE_PORT);
            mSocket = IO.socket(uri); // 创建指定地址和端口的套接字实例
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mSocket.connect(); // 建立Socket连接
        // 等待接收传来的图片数据
        mSocket.on("receive_image", (args) -> receiveImage(args));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == CHOOSE_CODE) { // 从相册返回
            if (intent.getData() != null) { // 从相册选择一张照片
                Uri uri = intent.getData(); // 获得已选择照片的路径对象
                String path = uri.toString();
                mFileName = path.substring(path.lastIndexOf("/")+1);
                // 根据指定图片的uri，获得自动缩小后的位图对象
                mBitmap = BitmapUtil.getAutoZoomImage(this, uri);
                iv_request.setImageBitmap(mBitmap); // 设置图像视图的位图对象
            }
        }
    }

    private int mBlock = 50*1024; // 每段的数据包大小
    // 分段传输图片数据
    private void sendImage() {
        Log.d(TAG, "sendImage");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把位图数据压缩到字节数组输出流
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] bytes = baos.toByteArray();
        int count = bytes.length/mBlock + 1;
        Log.d(TAG, "sendImage length="+bytes.length+", count="+count);
        // 下面把图片数据经过BASE64编码后发给Socket服务器
        for (int i=0; i<count; i++) {
            Log.d(TAG, "sendImage i="+i);
            String encodeData = "";
            if (i == count-1) { // 是最后一段图像数据
                int remain = bytes.length % mBlock;
                byte[] temp = new byte[remain];
                System.arraycopy(bytes, i*mBlock, temp, 0, remain);
                encodeData = Base64.encodeToString(temp, Base64.DEFAULT);
            } else { // 不是最后一段图像数据
                byte[] temp = new byte[mBlock];
                System.arraycopy(bytes, i*mBlock, temp, 0, mBlock);
                encodeData = Base64.encodeToString(temp, Base64.DEFAULT);
            }
            // 往Socket服务器发送本段的图片数据
            ImagePart part = new ImagePart(mFileName, encodeData, i, bytes.length);
            SocketUtil.emit(mSocket, "send_image", part); // 向服务器提交图像数据
        }
    }

    private String mLastFile; // 上次的文件名
    private int mReceiveCount; // 接收包的数量
    private byte[] mReceiveData; // 收到的字节数组
    // 接收对方传来的图片数据
    private void receiveImage(Object... args) {
        JSONObject json = (JSONObject) args[0];
        ImagePart part = new Gson().fromJson(json.toString(), ImagePart.class);
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
            String desc = String.format("%s 收到服务端消息：%s", DateUtil.getNowTime(), part.getName());
            runOnUiThread(() -> { // 回到主线程展示图片与描述文字
                tv_response.setText(desc);
                iv_response.setImageBitmap(bitmap);
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("receive_image"); // 取消接收传来的图片数据
        if (mSocket.connected()) { // 已经连上Socket服务器
            mSocket.disconnect(); // 断开Socket连接
        }
        mSocket.close(); // 关闭Socket连接
    }

}