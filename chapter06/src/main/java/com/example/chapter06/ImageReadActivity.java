package com.example.chapter06;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chapter06.util.FileUtil;
import com.example.chapter06.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageReadActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "ImageReadActivity";
    private TextView tv_content; // 声明一个文本视图对象
    private ImageView iv_content; // 声明一个图像视图对象
    private String mPath; // 私有目录路径
    private List<File> mFilelist = new ArrayList<File>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_read);
        tv_content = findViewById(R.id.tv_content);
        iv_content = findViewById(R.id.iv_content);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        // 获取当前App的私有下载目录
        mPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/";
        showFileContent(); // 显示最新的图片文件内容
    }

    // 显示最新的图片文件内容
    private void showFileContent() {
        // 获得指定目录下面的所有图片文件
        mFilelist = FileUtil.getFileList(mPath, new String[]{".jpeg"});
        if (mFilelist.size() > 0) {
            // 打开并显示选中的图片文件内容
            String file_path = mFilelist.get(0).getAbsolutePath();
            tv_content.setText("找到最新的图片文件，路径为"+file_path);
            // 显示存储卡图片文件的第一种方式：直接调用setImageURI方法
            //iv_content.setImageURI(Uri.parse(file_path)); // 设置图像视图的路径对象
            // 第二种方式：先调用decodeFile方法获得位图，再调用setImageBitmap方法
            //Bitmap bitmap = BitmapFactory.decodeFile(file_path);
            //iv_content.setImageBitmap(bitmap); // 设置图像视图的位图对象
            // 第三种方式：先调用FileUtil.openImage获得位图，再调用setImageBitmap方法
            Bitmap bitmap = FileUtil.openImage(file_path);
            iv_content.setImageBitmap(bitmap); // 设置图像视图的位图对象
        } else {
            tv_content.setText("私有目录下未找到任何图片文件");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_delete) {
            for (int i = 0; i < mFilelist.size(); i++) {
                // 获取该文件的绝对路径字符串
                String file_path = mFilelist.get(i).getAbsolutePath();
                File f = new File(file_path);
                if (!f.delete()) { // 删除文件，并判断是否成功删除
                    Log.d(TAG, "file_path=" + file_path + ", delete failed");
                }
            }
            ToastUtil.show(this, "已删除私有目录下的所有图片文件");
        }
    }

}
