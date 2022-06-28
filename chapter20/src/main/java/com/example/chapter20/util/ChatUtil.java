package com.example.chapter20.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chapter20.R;
import com.example.chapter20.widget.CircleDrawable;

public class ChatUtil {
    // 房间图片的资源数组
    public final static int[] mPortraitArray = {
            R.drawable.portrait01, R.drawable.portrait02, R.drawable.portrait03, R.drawable.portrait04,
            R.drawable.portrait05, R.drawable.portrait06, R.drawable.portrait07, R.drawable.portrait08,
            R.drawable.portrait09, R.drawable.portrait10, R.drawable.portrait11, R.drawable.portrait12,
            R.drawable.portrait13, R.drawable.portrait14, R.drawable.portrait15, R.drawable.portrait16
    };
    // 聊天文字的颜色数组
    public final static int[] mColorArray = {
            Color.BLACK, Color.BLUE, Color.CYAN, Color.GREEN, // 黑色、蓝色、青色、绿色
            Color.MAGENTA, Color.RED, Color.WHITE, Color.YELLOW, // 紫色、红色、白色、黄色
            Color.rgb(0x80, 0x2a, 0x2a), Color.rgb(0xfe, 0x2b, 0x54), // 棕色，玫红
            Color.rgb(0x08, 0x2e, 0x54), Color.rgb(0xda, 0x70, 0xd6), // 靛蓝，浅紫
            Color.rgb(0xff, 0xd7, 0x00), Color.rgb(0xaa, 0xaa, 0xff), // 金黄，淡蓝
            Color.rgb(0xff, 0x66, 0x66), Color.rgb(0x80, 0xff, 0x00) // 粉红，黄绿
    };

    // 根据昵称获取对应的头像
    public static Drawable getPortraitByName(Context ctx, String name) {
        Bitmap bitmap = getBitmapByName(ctx, name);
        CircleDrawable drawable = new CircleDrawable(ctx, bitmap);
        return drawable;
    }

    // 根据昵称获取对应的位图
    public static Bitmap getBitmapByName(Context ctx, String name) {
        String md5 = MD5Util.encrypt(name);
        char lastChar = md5.charAt(md5.length()-1);
        int pos = lastChar>='A' ? lastChar-'A'+10 : lastChar-'0';
        return BitmapFactory.decodeResource(ctx.getResources(), mPortraitArray[pos]);
    }

    // 获得一个消息内容的文本视图模板
    public static View getSimpleChatView(Context ctx, String name, String content, boolean isSelf) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.layout_message, null);
        LinearLayout ll_message = view.findViewById(R.id.ll_message);
        TextView tv_nickname = view.findViewById(R.id.tv_nickname);
        TextView tv_message = view.findViewById(R.id.tv_message);
        String md5 = MD5Util.encrypt(name);
        char lastChar = md5.charAt(md5.length()-1);
        int pos = lastChar>='A' ? lastChar-'A'+10 : lastChar-'0';
        int color = mColorArray[pos];
        ll_message.setBackgroundResource(R.drawable.shape_message_gray);
        tv_nickname.setText(name+"：");
        tv_nickname.setTextColor(color);
        tv_message.setText(content);
        return view;
    }

}
