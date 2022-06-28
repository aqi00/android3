package com.example.chapter13.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chapter13.ImageDetailActivity;
import com.example.chapter13.R;
import com.example.chapter13.widget.RoundDrawable;

public class ChatUtil {
    private static final String TAG = "ChatUtil";
    // 头像图片的资源数组
    public final static int[] mPortraitArray = {
            R.drawable.portrait01, R.drawable.portrait02, R.drawable.portrait03, R.drawable.portrait04,
            R.drawable.portrait05, R.drawable.portrait06, R.drawable.portrait07, R.drawable.portrait08,
            R.drawable.portrait09, R.drawable.portrait10, R.drawable.portrait11, R.drawable.portrait12,
            R.drawable.portrait13, R.drawable.portrait14, R.drawable.portrait15, R.drawable.portrait16
    };

    // 根据昵称获取对应的头像
    public static Drawable getPortraitByName(Context ctx, String name) {
        String md5 = MD5Util.encrypt(name);
        char lastChar = md5.charAt(md5.length()-1);
        int pos = lastChar>='A' ? lastChar-'A'+10 : lastChar-'0';
        Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(), mPortraitArray[pos]);
        RoundDrawable drawable = new RoundDrawable(ctx, bitmap);
        return drawable;
    }

    // 获得一个消息内容的视图模板
    public static View getChatView(Context ctx, String name, String content, boolean isSelf) {
        int layoutId = isSelf ? R.layout.chat_me : R.layout.chat_other;
        View view = LayoutInflater.from(ctx).inflate(layoutId, null);
        ImageView iv_portrait = view.findViewById(R.id.iv_portrait);
        TextView tv_content = view.findViewById(R.id.tv_content);
        iv_portrait.setImageDrawable(getPortraitByName(ctx, name));
        tv_content.setText(content);
        LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_params.gravity = isSelf ? Gravity.RIGHT : Gravity.LEFT;
        view.setLayoutParams(ll_params);
        return view;
    }

    // 获得一个提示内容的文本视图模板
    public static TextView getHintView(Context ctx, String content, int margin) {
        TextView tv = new TextView(ctx);
        tv.setText(content);
        tv.setTextSize(12);
        tv.setTextColor(Color.GRAY);
        LinearLayout.LayoutParams tv_params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tv_params.setMargins(margin, margin, margin, margin);
        tv_params.gravity = Gravity.CENTER;
        tv.setLayoutParams(tv_params);
        return tv;
    }

    // 获得一个消息图片的视图模板
    public static View getChatImage(Context ctx, String name, String imagePath, boolean isSelf) {
        int layoutId = isSelf ? R.layout.chat_me : R.layout.chat_other;
        View view = LayoutInflater.from(ctx).inflate(layoutId, null);
        ImageView iv_portrait = view.findViewById(R.id.iv_portrait);
        view.findViewById(R.id.tv_content).setVisibility(View.GONE);
        ImageView iv_content = view.findViewById(R.id.iv_content);
        iv_portrait.setImageDrawable(getPortraitByName(ctx, name));
        iv_content.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams iv_params = (LinearLayout.LayoutParams) iv_content.getLayoutParams();
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        RoundDrawable drawable = new RoundDrawable(ctx, bitmap);
        iv_content.setImageDrawable(drawable);
        iv_params.height = Utils.dip2px(ctx, 240 / (1.0f * bitmap.getWidth() / bitmap.getHeight() + 1));
        iv_params.width = Utils.dip2px(ctx, 240) - iv_params.height;
        Log.d(TAG, "iv_params.width="+iv_params.width+", iv_params.height="+iv_params.height);
        iv_content.setLayoutParams(iv_params);
        iv_content.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, ImageDetailActivity.class);
            intent.putExtra("imagePath", imagePath);
            ctx.startActivity(intent);
        });
        LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_params.gravity = isSelf ? Gravity.RIGHT : Gravity.LEFT;
        ll_params.gravity = ll_params.gravity | Gravity.TOP;
        view.setLayoutParams(ll_params);
        return view;
    }

}
