package com.example.chapter16;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chapter16.util.PermissionUtil;
import com.example.chapter16.util.SharedUtil;
import com.example.chapter16.util.SwitchUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_sensor).setOnClickListener(this);
        findViewById(R.id.btn_acceleration).setOnClickListener(this);
        findViewById(R.id.btn_direction).setOnClickListener(this);
        findViewById(R.id.btn_step).setOnClickListener(this);
        findViewById(R.id.btn_light).setOnClickListener(this);
        findViewById(R.id.btn_gyroscope).setOnClickListener(this);
        findViewById(R.id.btn_location_setting).setOnClickListener(this);
        findViewById(R.id.btn_location_begin).setOnClickListener(this);
        findViewById(R.id.btn_location_address).setOnClickListener(this);
        findViewById(R.id.btn_satellite_sphere).setOnClickListener(this);
        findViewById(R.id.btn_map_location).setOnClickListener(this);
        findViewById(R.id.btn_map_basic).setOnClickListener(this);
        findViewById(R.id.btn_map_search).setOnClickListener(this);
        findViewById(R.id.btn_map_navigation).setOnClickListener(this);
        findViewById(R.id.btn_nearby).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_sensor) {
            startActivity(new Intent(this, SensorActivity.class));
        } else if (v.getId() == R.id.btn_acceleration) {
            startActivity(new Intent(this, AccelerationActivity.class));
        } else if (v.getId() == R.id.btn_direction) {
            startActivity(new Intent(this, DirectionActivity.class));
        } else if (v.getId() == R.id.btn_step) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android10?????????????????????????????????????????????
                if (PermissionUtil.checkPermission(this, Manifest.permission.ACTIVITY_RECOGNITION, (int) v.getId() % 65536)) {
                    startActivity(new Intent(this, StepActivity.class));
                }
            } else {
                startActivity(new Intent(this, StepActivity.class));
            }
        } else if (v.getId() == R.id.btn_light) {
            if (SwitchUtil.checkWriteSettings(this, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, LightActivity.class));
            }
        } else if (v.getId() == R.id.btn_gyroscope) {
            startActivity(new Intent(this, GyroscopeActivity.class));
        } else if (v.getId() == R.id.btn_location_setting) {
            startActivity(new Intent(this, LocationSettingActivity.class));
        } else if (v.getId() == R.id.btn_location_begin) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, LocationBeginActivity.class));
            }
        } else if (v.getId() == R.id.btn_location_address) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, LocationAddressActivity.class));
            }
        } else if (v.getId() == R.id.btn_satellite_sphere) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, SatelliteSphereActivity.class));
            }
        } else if (v.getId() == R.id.btn_map_location) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, MapLocationActivity.class));
            }
        } else if (v.getId() == R.id.btn_map_basic) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, MapBasicActivity.class));
            }
        } else if (v.getId() == R.id.btn_map_search) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, MapSearchActivity.class));
            }
        } else if (v.getId() == R.id.btn_map_navigation) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, MapNavigationActivity.class));
            }
        } else if (v.getId() == R.id.btn_nearby) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, (int) v.getId() % 65536)) {
                gotoNearby();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // requestCode?????????????????????????????????2???16?????????65536
        if (requestCode == R.id.btn_step % 65536) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, StepActivity.class));
            } else {
                Toast.makeText(this, "??????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_light % 65536) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, LightActivity.class));
            } else {
                Toast.makeText(this, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_location_begin % 65536) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, LocationBeginActivity.class));
            } else {
                Toast.makeText(this, "?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_location_address % 65536) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, LocationAddressActivity.class));
            } else {
                Toast.makeText(this, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_satellite_sphere % 65536) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivity(new Intent(this, SatelliteSphereActivity.class));
            } else {
                Toast.makeText(this, "??????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_map_location % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // ???????????????????????????
                startActivity(new Intent(this, MapLocationActivity.class));
            } else {
                Toast.makeText(this, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_map_basic % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // ???????????????????????????
                startActivity(new Intent(this, MapBasicActivity.class));
            } else {
                Toast.makeText(this, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_map_search % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // ???????????????????????????
                startActivity(new Intent(this, MapSearchActivity.class));
            } else {
                Toast.makeText(this, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_map_navigation % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // ???????????????????????????
                startActivity(new Intent(this, MapNavigationActivity.class));
            } else {
                Toast.makeText(this, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_nearby % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // ???????????????????????????
                gotoNearby();
            } else {
                Toast.makeText(this, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void gotoNearby() {
        String commitMyInfo = SharedUtil.getIntance(this).readString("commitMyInfo", "false");
        if ("false".equals(commitMyInfo)) {
            startActivity(new Intent(this, ChooseLocationActivity.class));
        } else {
            startActivity(new Intent(this, NearbyActivity.class));
        }
    }
}
