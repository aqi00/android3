package com.example.chapter06;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter06.util.FileUtil;
import com.example.chapter06.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("SetTextI18n")
public class FileReadActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "FileReadActivity";
    private TextView tv_content; // 声明一个文本视图对象
    private String mPath; // 私有目录路径
    private List<File> mFilelist = new ArrayList<File>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_read);
        tv_content = findViewById(R.id.tv_content);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        if (getIntent().getBooleanExtra("is_external", false)) {
            // 获取当前App的公共下载目录
            mPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/";
        } else {
            // 获取当前App的私有下载目录
            mPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/";
        }
        showFileContent(); // 显示最新的文本文件内容
    }

    // 显示最新的文本文件内容
    private void showFileContent() {
        // 获得指定目录下面的所有文本文件
        mFilelist = FileUtil.getFileList(mPath, new String[]{".txt"});
        if (mFilelist.size() > 0) {
            // 打开并显示选中的文本文件内容
            String file_path = mFilelist.get(0).getAbsolutePath();
            String content = FileUtil.openText(file_path);
            String desc = String.format("找到最新的文本文件，路径为%s，内容如下：\n%s",
                    file_path, content);
            tv_content.setText(desc);
        } else {
            tv_content.setText("私有目录下未找到任何文本文件");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_delete) {
            for (int i = 0; i < mFilelist.size(); i++) {
                String file_path = mFilelist.get(i).getAbsolutePath();
                File f = new File(file_path);
                if (!f.delete()) {
                    Log.d(TAG, "file_path=" + file_path + ", delete failed");
                }
            }
            ToastUtil.show(this, "已删除私有目录下的所有文本文件");
        }
    }

}
