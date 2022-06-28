package com.example.chapter06;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FilePathActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_path);
        TextView tv_path = findViewById(R.id.tv_path);
        // Android7.0之后默认禁止访问公共存储目录
        // 获取系统的公共存储路径
        String publicPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        // 获取当前App的私有存储路径
        String privatePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
        boolean isLegacy = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android10的存储空间默认采取分区方式，此处判断是传统方式还是分区方式
            isLegacy = Environment.isExternalStorageLegacy();
        }
        String desc = "系统的公共存储路径位于" + publicPath +
                "\n\n当前App的私有存储路径位于" + privatePath +
                "\n\nAndroid7.0之后默认禁止访问公共存储目录" +
                "\n\n当前App的存储空间采取" + (isLegacy?"传统方式":"分区方式");
        tv_path.setText(desc);
    }

}
