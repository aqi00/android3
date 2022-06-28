package com.example.chapter17;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.chapter17.util.PermissionUtil;
import com.example.chapter17.widget.InputDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityResultLauncher mBlueLauncher1; // 声明一个活动结果启动器对象
    private ActivityResultLauncher mBlueLauncher2; // 声明一个活动结果启动器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_wifi_info).setOnClickListener(this);
        findViewById(R.id.btn_wifi_scan).setOnClickListener(this);
        findViewById(R.id.btn_nfc).setOnClickListener(this);
        findViewById(R.id.btn_infrared).setOnClickListener(this);
        findViewById(R.id.btn_bluetooth_pair).setOnClickListener(this);
        findViewById(R.id.btn_bluetooth_trans).setOnClickListener(this);
        findViewById(R.id.btn_ble_scan).setOnClickListener(this);
        findViewById(R.id.btn_ble_advertise).setOnClickListener(this);
        findViewById(R.id.btn_ble_chat).setOnClickListener(this);
        findViewById(R.id.btn_scan_car).setOnClickListener(this);
        // 注册一个善后工作的活动结果启动器
        mBlueLauncher1 = registerForActivityResult(new ActivityResultContracts.RequestPermission(), it -> {
            if (it) {
                mBlueLauncher2.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            } else {
                Toast.makeText(this, "未获得蓝牙连接权限，无法使用蓝牙功能", Toast.LENGTH_SHORT).show();
            }
        });
        // 注册一个善后工作的活动结果启动器
        mBlueLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() != RESULT_OK) {
                Toast.makeText(this, "需要允许蓝牙连接权限才能使用蓝牙功能噢", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_wifi_info) {
            startActivity(new Intent(this, WifiInfoActivity.class));
        } else if (v.getId() == R.id.btn_wifi_scan) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Toast.makeText(this, "扫描周边WiFi需要Android6或更高版本", Toast.LENGTH_SHORT).show();
                return;
            }
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, WifiScanActivity.class));
            }
        } else if (v.getId() == R.id.btn_nfc) {
            startActivity(new Intent(this, NfcActivity.class));
        } else if (v.getId() == R.id.btn_infrared) {
            startActivity(new Intent(this, InfraredActivity.class));
        } else if (v.getId() == R.id.btn_bluetooth_pair) {
            // Android12之后使用蓝牙需要蓝牙连接权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                    mBlueLauncher1.launch(Manifest.permission.BLUETOOTH_CONNECT);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android6.0之后使用蓝牙需要定位权限
                if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, (int) v.getId() % 65536)) {
                    startActivity(new Intent(this, BluetoothPairActivity.class));
                }
            } else {
                startActivity(new Intent(this, BluetoothPairActivity.class));
            }
        } else if (v.getId() == R.id.btn_bluetooth_trans) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android12之后使用蓝牙需要蓝牙连接权限，Android6.0之后使用蓝牙需要定位权限
                if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, (int) v.getId() % 65536)) {
                    startActivity(new Intent(this, BluetoothTransActivity.class));
                }
            } else {
                startActivity(new Intent(this, BluetoothTransActivity.class));
            }
        } else if (v.getId() == R.id.btn_ble_scan) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android12之后使用蓝牙需要蓝牙连接权限，Android6.0之后使用蓝牙需要定位权限
                if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, (int) v.getId() % 65536)) {
                    startActivity(new Intent(this, BleScanActivity.class));
                }
            } else {
                startActivity(new Intent(this, BleScanActivity.class));
            }
        } else if (v.getId() == R.id.btn_ble_advertise) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android12之后使用蓝牙需要蓝牙连接权限，Android6.0之后使用蓝牙需要定位权限
                if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, (int) v.getId() % 65536)) {
                    startActivity(new Intent(this, BleAdvertiseActivity.class));
                }
            } else {
                startActivity(new Intent(this, BleAdvertiseActivity.class));
            }
        } else if (v.getId() == R.id.btn_ble_chat) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android12之后使用蓝牙需要蓝牙连接权限，Android6.0之后使用蓝牙需要定位权限
                if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, (int) v.getId() % 65536)) {
                    gotoChat();
                }
            } else {
                gotoChat();
            }
        } else if (v.getId() == R.id.btn_scan_car) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android12之后使用蓝牙需要蓝牙连接权限，Android6.0之后使用蓝牙需要定位权限
                if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, (int) v.getId() % 65536)) {
                    startActivity(new Intent(this, ScanCarActivity.class));
                }
            } else {
                startActivity(new Intent(this, ScanCarActivity.class));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // requestCode不能为负数，也不能大于2的16次方即65536
        if (requestCode == R.id.btn_wifi_scan % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(this, WifiScanActivity.class));
            } else {
                Toast.makeText(this, "需要允许定位权限才能扫描周边的WiFi噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_bluetooth_pair % 65536) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, BluetoothPairActivity.class));
            } else {
                Toast.makeText(this, "需要允许定位权限才能使用传统蓝牙噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_bluetooth_trans % 65536) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, BluetoothTransActivity.class));
            } else {
                Toast.makeText(this, "需要允许定位权限才能使用传统蓝牙噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_ble_scan % 65536) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, BleScanActivity.class));
            } else {
                Toast.makeText(this, "需要允许定位权限才能使用低功耗蓝牙噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_ble_advertise % 65536) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, BleAdvertiseActivity.class));
            } else {
                Toast.makeText(this, "需要允许定位权限才能使用低功耗蓝牙噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_ble_chat % 65536) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gotoChat(); // 跳到聊天界面
            } else {
                Toast.makeText(this, "需要允许定位权限才能使用低功耗蓝牙噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_scan_car % 65536) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, ScanCarActivity.class));
            } else {
                Toast.makeText(this, "需要允许定位权限才能遥控智能小车噢", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 跳到聊天界面
    private void gotoChat() {
        // 弹出服务器输入对话框，以便决定作为BLE客户端还是作为BLE服务端
        InputDialog dialog = new InputDialog(this, "", 0, "请输入服务名，不填则为客户端",
                (idt, content, seq) -> {
                    if (TextUtils.isEmpty(content)) {
                        startActivity(new Intent(this, BleClientActivity.class));
                    } else {
                        Intent intent = new Intent(this, BleServerActivity.class);
                        intent.putExtra("server_name", content);
                        startActivity(intent);
                    }
                });
        dialog.show();
    }

}
