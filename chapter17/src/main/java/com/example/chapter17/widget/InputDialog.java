package com.example.chapter17.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chapter17.R;

public class InputDialog {
    private Dialog mDialog; // 声明一个对话框对象
    private View mView; // 声明一个视图对象
    private String mIdt; // 当前标识
    private int mSeq; // 当前序号
    private String mTitle; // 对话框标题
    private InputCallbacks mCallbacks; // 回调监听器

    public InputDialog(Context context, String idt, int seq, String title, InputCallbacks callbacks) {
        mIdt = idt;
        mSeq = seq;
        mTitle = title;
        mCallbacks = callbacks;
        // 根据布局文件dialog_input.xml生成视图对象
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_input, null);
        // 创建一个指定风格的对话框对象
        mDialog = new Dialog(context, R.style.CustomDialog);
        TextView tv_title = mView.findViewById(R.id.tv_title);
        EditText et_input = mView.findViewById(R.id.et_input);
        tv_title.setText(mTitle);
        mView.findViewById(R.id.tv_cancel).setOnClickListener(v -> dismiss());
        mView.findViewById(R.id.tv_confirm).setOnClickListener(v -> {
            dismiss(); // 关闭对话框
            mCallbacks.onInput(mIdt, et_input.getText().toString(), mSeq);
        });
    }

    // 显示对话框
    public void show() {
        // 设置对话框窗口的内容视图
        mDialog.getWindow().setContentView(mView);
        // 设置对话框窗口的布局参数
        mDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mDialog.show(); // 显示对话框
    }

    // 关闭对话框
    public void dismiss() {
        // 如果对话框显示出来了，就关闭它
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss(); // 关闭对话框
        }
    }

    // 判断对话框是否显示
    public boolean isShowing() {
        if (mDialog != null) {
            return mDialog.isShowing();
        } else {
            return false;
        }
    }

    public interface InputCallbacks {
        void onInput(String idt, String content, int seq);
    }

}
