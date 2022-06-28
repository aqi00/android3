package com.example.chapter06;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter06.bean.Contact;
import com.example.chapter06.util.CommunicationUtil;
import com.example.chapter06.util.ToastUtil;
import com.example.chapter06.util.Utils;

import java.util.List;

public class ContactReadActivity extends AppCompatActivity {
    private TextView tv_desc; // 声明一个文本视图对象
    private LinearLayout ll_list; // 联系人列表的线性布局

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_read);
        tv_desc = findViewById(R.id.tv_desc);
        ll_list = findViewById(R.id.ll_list);
        showContactInfo(); // 显示所有的联系人信息
    }

    // 显示所有的联系人信息
    private void showContactInfo() {
        try {
            // 读取所有的联系人
            List<Contact> contactList = CommunicationUtil.readAllContacts(getContentResolver());
            String contactCount = String.format("当前共找到%d位联系人", contactList.size());
            tv_desc.setText(contactCount);
            for (Contact contact : contactList){
                String contactDesc = String.format("姓名为%s，号码为%s",contact.name, contact.phone);
                TextView tv_contact = new TextView(this); // 创建一个文本视图
                tv_contact.setText(contactDesc);
                tv_contact.setTextColor(Color.BLACK);
                tv_contact.setTextSize(17);
                int pad = Utils.dip2px(this, 5);
                tv_contact.setPadding(pad, pad, pad, pad); // 设置文本视图的内部间距
                ll_list.addView(tv_contact); // 把文本视图添加至联系人列表的线性布局
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(this, "请检查是否开启了通讯录权限");
        }
    }

}
