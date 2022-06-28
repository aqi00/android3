package com.example.chapter06;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chapter06.util.DateUtil;
import com.example.chapter06.util.FileUtil;
import com.example.chapter06.util.ToastUtil;

public class ImageWriteActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_content; // 声明一个图像视图对象
    private TextView tv_path; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_write);
        iv_content = findViewById(R.id.iv_content);
        iv_content.setImageResource(R.drawable.huawei); // 设置图像视图的图片资源
        tv_path = findViewById(R.id.tv_path);
        findViewById(R.id.btn_save).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save) {
            // 获取当前App的私有下载目录
            String path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/";
            // 从指定的资源文件中获取位图对象
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.huawei);
            String file_path = path + DateUtil.getNowDateTime("") + ".jpeg";
            FileUtil.saveImage(file_path, bitmap); // 把位图对象保存为图片文件
            tv_path.setText("图片文件的保存路径为：\n" + file_path);
            ToastUtil.show(this, "图片已写入存储卡文件");
        }
    }

}
