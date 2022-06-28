package com.example.chapter16.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chapter16.R;
import com.example.chapter16.bean.PersonInfo;
import com.example.chapter16.constant.UrlConstant;

@SuppressLint("SetTextI18n")
public class PersonDialog {
    private Dialog mDialog; // 声明一个对话框对象
    private View mView; // 声明一个视图对象
    private PersonInfo mPersonInfo; // 人员信息
    private PersonCallBack mCallback; // 回调监听器

    public PersonDialog(Context context, PersonInfo personInfo, PersonCallBack callback) {
        mPersonInfo = personInfo;
        mCallback = callback;
        // 根据布局文件dialog_person.xml生成视图对象
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_person, null);
        // 创建一个指定风格的对话框对象
        mDialog = new Dialog(context, R.style.CustomDialog);
        TextView tv_title = mView.findViewById(R.id.tv_title);
        TextView tv_sex = mView.findViewById(R.id.tv_sex);
        ImageView iv_face = mView.findViewById(R.id.iv_face);
        TextView tv_phone = mView.findViewById(R.id.tv_phone);
        TextView tv_love = mView.findViewById(R.id.tv_love);
        TextView tv_address = mView.findViewById(R.id.tv_address);
        TextView tv_info = mView.findViewById(R.id.tv_info);
        Button btn_dial = mView.findViewById(R.id.btn_dial);
        Button btn_navigate = mView.findViewById(R.id.btn_navigate);
        tv_title.setText(mPersonInfo.getName()+"的个人信息");
        // 使用Glide加载圆形裁剪后的人员头像
        Glide.with(context).load(UrlConstant.HTTP_PREFIX+mPersonInfo.getFace()).circleCrop().into(iv_face);
        tv_sex.setText("性别："+((mPersonInfo.getSex()==0)?"男":"女"));
        tv_phone.setText("手机号："+mPersonInfo.getPhone());
        tv_love.setText("爱好："+mPersonInfo.getLove());
        tv_address.setText("地址："+mPersonInfo.getAddress());
        tv_info.setText("发布信息："+mPersonInfo.getInfo());
        String callName = (mPersonInfo.getSex()==0)?"他":"她";
        btn_dial.setText("打"+callName+"电话");
        btn_navigate.setText("去"+callName+"那里");
        btn_dial.setOnClickListener(v -> { // 处理拨号动作
            dismiss(); // 关闭对话框
            mCallback.onDial(mPersonInfo);
        });
        btn_navigate.setOnClickListener(v -> { // 处理导航动作
            dismiss(); // 关闭对话框
            mCallback.onNavigate(mPersonInfo);
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

    // 定义一个人员动作监听器
    public interface PersonCallBack {
        void onDial(PersonInfo person); // 打他电话
        void onNavigate(PersonInfo person); // 去他那里
    }

}
