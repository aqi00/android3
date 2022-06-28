package com.example.chapter15.fragment;

import android.content.Context;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.chapter15.DjvuRenderActivity;
import com.example.chapter15.R;
import com.example.chapter15.util.BitmapUtil;

import java.io.File;

public class ImageFragment extends Fragment {
    private static final String TAG = "ImageFragment";
    protected View mView;
    protected Context mContext;
    private String mPath; // 书页图片的文件路径
    private ImageView iv_content; // 声明一个图像视图对象

    public static ImageFragment newInstance(String path) {
        ImageFragment fragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        if (getArguments() != null) {
            mPath = getArguments().getString("path");
        }
        Log.d(TAG, "path=" + mPath);
        mView = inflater.inflate(R.layout.fragment_image, container, false);
        iv_content = mView.findViewById(R.id.iv_content);
        if (mPath.startsWith("http")) {
            Glide.with(this).load(mPath).into(iv_content);
        } else if ((new File(mPath)).exists()) { // 图片文件已经存在
            iv_content.setImageURI(Uri.parse(mPath)); // 设置图像视图的路径对象
        } else {
            readImage(); // 读取该书页的图像
        }
        return mView;
    }

    // 存储卡上没有该页的图片，就要到电子书中解析出该页的图像
    private void readImage() {
        String dir = mPath.substring(0, mPath.lastIndexOf("/"));
        final int index = Integer.parseInt(mPath.substring(mPath.lastIndexOf("/") + 1, mPath.lastIndexOf(".")));
        // 解析页面的操作是异步的，解析结果在监听器中回调通知
        DjvuRenderActivity.decodeService.decodePage(dir, index, bitmap -> {
            // 把位图对象保存成图片，下次直接读取存储卡上的图片文件
            BitmapUtil.saveImage(mPath, bitmap);
            // 解码监听器在分线程中运行，调用runOnUiThread方法表示回到主线程操作界面
            getActivity().runOnUiThread(() -> iv_content.setImageBitmap(bitmap));
        }, 1, new RectF(0, 0, 1, 1));
    }

}
