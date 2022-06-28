package com.example.chapter06;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter06.util.PermissionUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_share_write).setOnClickListener(this);
        findViewById(R.id.btn_share_read).setOnClickListener(this);
        findViewById(R.id.btn_login_share).setOnClickListener(this);
        findViewById(R.id.btn_datastore_write).setOnClickListener(this);
        findViewById(R.id.btn_datastore_read).setOnClickListener(this);
        findViewById(R.id.btn_sqlite_create).setOnClickListener(this);
        findViewById(R.id.btn_sqlite_write).setOnClickListener(this);
        findViewById(R.id.btn_sqlite_read).setOnClickListener(this);
        findViewById(R.id.btn_login_sqlite).setOnClickListener(this);
        findViewById(R.id.btn_file_path).setOnClickListener(this);
        findViewById(R.id.btn_file_write).setOnClickListener(this);
        findViewById(R.id.btn_file_read).setOnClickListener(this);
        findViewById(R.id.btn_image_write).setOnClickListener(this);
        findViewById(R.id.btn_image_read).setOnClickListener(this);
        findViewById(R.id.btn_external_write).setOnClickListener(this);
        findViewById(R.id.btn_external_read).setOnClickListener(this);
        findViewById(R.id.btn_app_life).setOnClickListener(this);
        findViewById(R.id.btn_app_write).setOnClickListener(this);
        findViewById(R.id.btn_app_read).setOnClickListener(this);
        findViewById(R.id.btn_room_write).setOnClickListener(this);
        findViewById(R.id.btn_room_read).setOnClickListener(this);
        findViewById(R.id.btn_content_write).setOnClickListener(this);
        findViewById(R.id.btn_content_read).setOnClickListener(this);
        findViewById(R.id.btn_contact_add).setOnClickListener(this);
        findViewById(R.id.btn_contact_read).setOnClickListener(this);
        findViewById(R.id.btn_monitor_sms).setOnClickListener(this);
        findViewById(R.id.btn_shopping_cart).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_share_write) {
            startActivity(new Intent(this, ShareWriteActivity.class));
        } else if (v.getId() == R.id.btn_share_read) {
            startActivity(new Intent(this, ShareReadActivity.class));
        } else if (v.getId() == R.id.btn_login_share) {
            startActivity(new Intent(this, LoginShareActivity.class));
        } else if (v.getId() == R.id.btn_datastore_write) {
            startActivity(new Intent(this, DatastoreWriteActivity.class));
        } else if (v.getId() == R.id.btn_datastore_read) {
            startActivity(new Intent(this, DatastoreReadActivity.class));
        } else if (v.getId() == R.id.btn_login_share) {
            startActivity(new Intent(this, LoginStoreActivity.class));
        } else if (v.getId() == R.id.btn_sqlite_create) {
            startActivity(new Intent(this, DatabaseActivity.class));
        } else if (v.getId() == R.id.btn_sqlite_write) {
            startActivity(new Intent(this, SQLiteWriteActivity.class));
        } else if (v.getId() == R.id.btn_sqlite_read) {
            startActivity(new Intent(this, SQLiteReadActivity.class));
        } else if (v.getId() == R.id.btn_login_sqlite) {
            startActivity(new Intent(this, LoginSQLiteActivity.class));
        } else if (v.getId() == R.id.btn_file_path) {
            startActivity(new Intent(this, FilePathActivity.class));
        } else if (v.getId() == R.id.btn_file_write) {
            startActivity(new Intent(this, FileWriteActivity.class));
        } else if (v.getId() == R.id.btn_file_read) {
            startActivity(new Intent(this, FileReadActivity.class));
        } else if (v.getId() == R.id.btn_image_write) {
            startActivity(new Intent(this, ImageWriteActivity.class));
        } else if (v.getId() == R.id.btn_image_read) {
            startActivity(new Intent(this, ImageReadActivity.class));
        } else if (v.getId() == R.id.btn_external_write) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_external_write % 65536)) {
                Intent intent = new Intent(this, FileWriteActivity.class);
                intent.putExtra("is_external", true);
                startActivity(intent);
            }
        } else if (v.getId() == R.id.btn_external_read) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.btn_external_read % 65536)) {
                Intent intent = new Intent(this, FileReadActivity.class);
                intent.putExtra("is_external", true);
                startActivity(intent);
            }
        } else if (v.getId() == R.id.btn_app_life) {
            startActivity(new Intent(this, ActTestActivity.class));
        } else if (v.getId() == R.id.btn_app_write) {
            startActivity(new Intent(this, AppWriteActivity.class));
        } else if (v.getId() == R.id.btn_app_read) {
            startActivity(new Intent(this, AppReadActivity.class));
        } else if (v.getId() == R.id.btn_room_write) {
            startActivity(new Intent(this, RoomWriteActivity.class));
        } else if (v.getId() == R.id.btn_room_read) {
            startActivity(new Intent(this, RoomReadActivity.class));
        } else if (v.getId() == R.id.btn_content_write) {
            startActivity(new Intent(this, ContentWriteActivity.class));
        } else if (v.getId() == R.id.btn_content_read) {
            startActivity(new Intent(this, ContentReadActivity.class));
        } else if (v.getId() == R.id.btn_contact_add) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_CONTACTS, R.id.btn_contact_add % 65536)) {
                startActivity(new Intent(this, ContactAddActivity.class));
            }
        } else if (v.getId() == R.id.btn_contact_read) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.READ_CONTACTS, R.id.btn_contact_read % 65536)) {
                startActivity(new Intent(this, ContactReadActivity.class));
            }
        } else if (v.getId() == R.id.btn_monitor_sms) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, R.id.btn_monitor_sms % 65536)) {
                startActivity(new Intent(this, MonitorSmsActivity.class));
            }
        } else if (v.getId() == R.id.btn_shopping_cart) {
            startActivity(new Intent(this, ShoppingCartActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // requestCode不能为负数，也不能大于2的16次方即65536
        if (requestCode == R.id.btn_external_write % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                Intent intent = new Intent(this, FileWriteActivity.class);
                intent.putExtra("is_external", true);
                startActivity(intent);
            } else {
                //ToastUtil.show(this, "需要允许存储卡权限才能写入公共空间噢");
                Toast.makeText(this, "需要允许存储卡权限才能写入公共空间噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_external_read % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                Intent intent = new Intent(this, FileReadActivity.class);
                intent.putExtra("is_external", true);
                startActivity(intent);
            } else {
                Toast.makeText(this, "需要允许存储卡权限才能读取公共空间噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_contact_add % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, ContactAddActivity.class));
            } else {
                Toast.makeText(this, "需要允许通讯录权限才能读写联系人噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_contact_read % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, ContactReadActivity.class));
            } else {
                Toast.makeText(this, "需要允许通讯录权限才能读写联系人噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_monitor_sms % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, MonitorSmsActivity.class));
            } else {
                Toast.makeText(this, "需要允许短信权限才能校准流量噢", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
