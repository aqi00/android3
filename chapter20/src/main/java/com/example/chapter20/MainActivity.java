package com.example.chapter20;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chapter20.constant.ChatConst;
import com.example.chapter20.util.PermissionUtil;
import com.example.chapter20.util.SocketUtil;
import com.example.chapter20.widget.InputDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_remote_video).setOnClickListener(this);
        findViewById(R.id.btn_contact_list).setOnClickListener(this);
        findViewById(R.id.btn_live_list).setOnClickListener(this);
        // 检查能否连上Socket服务器
        SocketUtil.checkSocketAvailable(this, ChatConst.CHAT_IP, ChatConst.CHAT_PORT);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_remote_video) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, (int) v.getId() % 65536)) {
                gotoRemote(); // 跳到远程页面
            }
        } else if (v.getId() == R.id.btn_contact_list) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, (int) v.getId() % 65536)) {
                gotoList(ContactListActivity.class); // 跳到联系人列表页面
            }
        } else if (v.getId() == R.id.btn_live_list) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, (int) v.getId() % 65536)) {
                gotoList(LiveListActivity.class); // 跳到直播房间列表页面
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // requestCode不能为负数，也不能大于2的16次方即65536
        if (requestCode == R.id.btn_remote_video % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                gotoRemote(); // 跳到远程页面
            } else {
                Toast.makeText(this, "需要允许摄像头和录音权限才能视频通话噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_contact_list % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                gotoList(ContactListActivity.class); // 跳到联系人列表页面
            } else {
                Toast.makeText(this, "需要允许摄像头权限才能视频通话噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_live_list % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                gotoList(LiveListActivity.class); // 跳到直播房间列表页面
            } else {
                Toast.makeText(this, "需要允许摄像头权限才能在线直播噢", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 跳到远程页面
    private void gotoRemote() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("远程视频");
        builder.setMessage("请选择作为视频服务的提供方还是接收方？");
        builder.setNegativeButton("接收方", (dialog, which) -> {
            startActivity(new Intent(this, VideoRecipientActivity.class));
        });
        builder.setPositiveButton("提供方", (dialog, which) -> {
            startActivity(new Intent(this, VideoOfferActivity.class));
        });
        builder.create().show();
    }

    // 跳到列表页面
    private void gotoList(Class<?> cls) {
        // 弹出服务器输入对话框，以便决定作为BLE客户端还是作为BLE服务端
        InputDialog dialog = new InputDialog(this, "", 0, "请填写你的昵称",
                (idt, content, seq) -> {
                    Intent intent = new Intent(this, cls);
                    intent.putExtra("self_name", content);
                    startActivity(intent);
                });
        dialog.show();
    }

}
