package com.example.chapter14.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.chapter14.util.BitmapUtil;
import com.example.chapter14.util.Utils;

import java.util.List;

public class PhotoGridAdapter extends BaseAdapter {
    private Context mContext; // 声明一个上下文对象
    private List<String> mPathList; // 图片文件的路径列表

    public PhotoGridAdapter(Context context, List<String> pathList) {
        mContext = context;
        mPathList = pathList;
    }

    // 获取列表项的个数
    @Override
    public int getCount() {
        return mPathList.size();
    }

    // 获取列表项的对象
    @Override
    public Object getItem(int position) {
        return mPathList.get(position);
    }

    // 获取列表项的编号
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 获取列表项的视图
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView iv_photo = new ImageView(mContext);
        ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(mContext, 200));
        iv_photo.setLayoutParams(param);
        Bitmap bitmap = BitmapFactory.decodeFile(mPathList.get(position));
        iv_photo.setImageBitmap(BitmapUtil.getAutoZoomImage(bitmap));
        return iv_photo;
    }
}
