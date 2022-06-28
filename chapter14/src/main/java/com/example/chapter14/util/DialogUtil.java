package com.example.chapter14.util;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class DialogUtil {

    // 显示信息对话框
    public static void showDialog(Context ctx, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(content);
        builder.setPositiveButton("好的", null);
        builder.create().show();
    }

}
