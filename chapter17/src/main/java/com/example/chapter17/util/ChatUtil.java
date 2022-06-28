package com.example.chapter17.util;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chapter17.R;

import java.util.ArrayList;
import java.util.List;

public class ChatUtil {

    // 获得一个消息内容的文本视图模板
    public static View getChatView(Context ctx, String content, boolean isSelf) {
        TextView tv_content;
        int layoutId = isSelf ? R.layout.chat_me : R.layout.chat_other;
        View view = LayoutInflater.from(ctx).inflate(layoutId, null);
        tv_content = view.findViewById(R.id.tv_content);
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

    // 按照字节数上限分割字符串
    public static List<String> splitString(String message, int byteLimit){
        List<String> msgList = new ArrayList<>();
        String res = "";
        int count = 0;
        for (int i = 0; i < message.length(); i++) {
            String tmpStr = message.substring(i,i+1);
            res += tmpStr;
            count += tmpStr.getBytes().length;
            if(count >= byteLimit-2){
                msgList.add(res);
                res = "";
                count = 0;
            }
            if(i == message.length()-1 && count < byteLimit){
                msgList.add(res);
            }
        }
        return msgList;
    }

}
