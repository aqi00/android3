package com.example.chapter06;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter06.bean.Contact;
import com.example.chapter06.util.CommunicationUtil;
import com.example.chapter06.util.ToastUtil;

@SuppressLint("DefaultLocale")
public class ContactAddActivity extends AppCompatActivity implements OnClickListener {
    private static final String TAG = "ContactAddActivity";
    private EditText et_contact_name; // 声明一个编辑框对象
    private EditText et_contact_phone; // 声明一个编辑框对象
    private EditText et_contact_email; // 声明一个编辑框对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_add);
        et_contact_name = findViewById(R.id.et_contact_name);
        et_contact_phone = findViewById(R.id.et_contact_phone);
        et_contact_email = findViewById(R.id.et_contact_email);
        findViewById(R.id.btn_add_contact).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add_contact) {
            Contact contact = new Contact(); // 创建一个联系人对象
            contact.name = et_contact_name.getText().toString().trim();
            contact.phone = et_contact_phone.getText().toString().trim();
            contact.email = et_contact_email.getText().toString().trim();
            // 方式一，使用ContentResolver多次写入，每次一个字段
            CommunicationUtil.addContacts(getContentResolver(), contact);
            // 方式二，使用ContentProviderOperation一次写入，每次多个字段
            //CommunicationUtil.addFullContacts(getContentResolver(), contact);
            ToastUtil.show(this, "成功添加联系人信息");
        }
    }

}
