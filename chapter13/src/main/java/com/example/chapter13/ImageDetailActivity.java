package com.example.chapter13;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
        String imagePath = getIntent().getStringExtra("imagePath");
        ImageView iv_photo = findViewById(R.id.iv_photo);
        iv_photo.setImageURI(Uri.parse(imagePath)); // 设置图像视图的路径对象
        iv_photo.setOnClickListener(v -> finish());
    }
}