package com.example.chapter04;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.example.chapter04.util.BitmapUtil;

public class ChoosePhotoActivity extends AppCompatActivity {
    private final static String TAG = "ChoosePhotoActivity";
    private int CHOOSE_CODE = 3; // 只在相册挑选图片的请求码
    private ImageView iv_photo; // 声明一个图像视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_photo);
        iv_photo = findViewById(R.id.iv_photo);
        findViewById(R.id.btn_choose_common).setOnClickListener(v -> {
            // 创建一个内容获取动作的意图（准备跳到系统相册）
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, CHOOSE_CODE);
        });
        // 注册一个善后工作的活动结果启动器，获取指定类型的内容
        ActivityResultLauncher launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                // 根据指定图片的uri，获得自动缩小后的位图对象
                Bitmap bitmap = BitmapUtil.getAutoZoomImage(this, uri);
                iv_photo.setImageBitmap(bitmap); // 设置图像视图的位图对象
            }
        });
        // 点击按钮时触发活动结果启动器，传入待获取内容的文件类型
        findViewById(R.id.btn_choose_register).setOnClickListener(v -> launcher.launch("image/*"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode==RESULT_OK && requestCode==CHOOSE_CODE) { // 从相册回来
            if (intent.getData() != null) {
                Uri uri = intent.getData(); // 获得已选择照片的路径对象
                // 根据指定图片的uri，获得自动缩小后的位图对象
                Bitmap bitmap = BitmapUtil.getAutoZoomImage(this, uri);
                iv_photo.setImageBitmap(bitmap); // 设置图像视图的位图对象
            }
        }
    }

}