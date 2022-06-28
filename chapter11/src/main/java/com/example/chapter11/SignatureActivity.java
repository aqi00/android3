package com.example.chapter11;

import com.example.chapter11.util.BitmapUtil;
import com.example.chapter11.util.DateUtil;
import com.example.chapter11.widget.SignatureView;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class SignatureActivity extends AppCompatActivity implements OnClickListener {
    private final static String TAG = "SignatureActivity";
    private SignatureView view_signature; // 声明一个签名视图对象
    private ImageView iv_signature_new; // 声明一个图像视图对象
    private String mImagePath; // 签名图片的文件路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        view_signature = findViewById(R.id.view_signature);
        iv_signature_new = findViewById(R.id.iv_signature_new);
        findViewById(R.id.btn_begin_signature).setOnClickListener(this);
        findViewById(R.id.btn_end_signature).setOnClickListener(this);
        findViewById(R.id.btn_reset_signature).setOnClickListener(this);
        findViewById(R.id.btn_revoke_signature).setOnClickListener(this);
        findViewById(R.id.btn_save_signature).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save_signature) { // 点击了保存签名按钮
            if (TextUtils.isEmpty(mImagePath)) {
                Toast.makeText(this, "请先开始然后结束签名", Toast.LENGTH_LONG).show();
                return;
            }
            BitmapUtil.notifyPhotoAlbum(this, mImagePath); // 通知相册来了张新图片
            Toast.makeText(this, "已保存签名图片，请到系统相册查看", Toast.LENGTH_LONG).show();
        } else if (v.getId() == R.id.btn_begin_signature) { // 点击了开始签名按钮
            // 开启签名视图的绘图缓存
            view_signature.setDrawingCacheEnabled(true);
        } else if (v.getId() == R.id.btn_reset_signature) { // 点击了重置按钮
            view_signature.clear(); // 清空签名视图
        } else if (v.getId() == R.id.btn_revoke_signature) { // 点击了回退按钮
            view_signature.revoke(); // 回退签名视图的最近一笔绘画
        } else if (v.getId() == R.id.btn_end_signature) { // 点击了结束签名按钮
            if (!view_signature.isDrawingCacheEnabled()) { // 签名视图的绘图缓存不可用
                Toast.makeText(this, "请先开始签名", Toast.LENGTH_LONG).show();
            } else { // 签名视图的绘图缓存当前可用
                Bitmap bitmap = view_signature.getDrawingCache(); // 从绘图缓存获取位图对象
                // 生成图片文件的保存路径
                mImagePath = String.format("%s/%s.jpg",
                        getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),
                        DateUtil.getNowDateTime());
                BitmapUtil.saveImage(mImagePath, bitmap); // 把位图保存为图片文件
                iv_signature_new.setImageURI(Uri.parse(mImagePath)); // 设置图像视图的路径对象
                // 延迟100毫秒后启动绘图缓存的重置任务
                new Handler(Looper.myLooper()).postDelayed(() -> {
                    // 关闭签名视图的绘图缓存
                    view_signature.setDrawingCacheEnabled(false);
                    // 开启签名视图的绘图缓存
                    view_signature.setDrawingCacheEnabled(true);
                }, 100);
            }
        }
    }

}
