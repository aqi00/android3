package com.example.chapter14;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter14.util.PermissionUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_audio_record).setOnClickListener(this);
        findViewById(R.id.btn_audio_play).setOnClickListener(this);
        findViewById(R.id.btn_media_recorder).setOnClickListener(this);
        findViewById(R.id.btn_photo_take).setOnClickListener(this);
        findViewById(R.id.btn_video_record).setOnClickListener(this);
        findViewById(R.id.btn_video_play).setOnClickListener(this);
        findViewById(R.id.btn_video_frame).setOnClickListener(this);
        findViewById(R.id.btn_camerax_photo).setOnClickListener(this);
        findViewById(R.id.btn_camerax_record).setOnClickListener(this);
        findViewById(R.id.btn_exo_player).setOnClickListener(this);
        findViewById(R.id.btn_short_view).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_audio_record) {
            startActivity(new Intent(this, AudioRecordActivity.class));
        } else if (v.getId() == R.id.btn_audio_play) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, R.id.btn_audio_play % 65536)) {
                startActivity(new Intent(this, AudioPlayActivity.class));
            }
        } else if (v.getId() == R.id.btn_media_recorder) {
            if (PermissionUtil.checkPermission(this, Manifest.permission.RECORD_AUDIO, R.id.btn_media_recorder % 65536)) {
                startActivity(new Intent(this, MediaRecorderActivity.class));
            }
        } else if (v.getId() == R.id.btn_photo_take) {
            if (PermissionUtil.checkPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, R.id.btn_photo_take % 65536)) {
                startActivity(new Intent(this, PhotoTakeActivity.class));
            }
        } else if (v.getId() == R.id.btn_video_record) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, R.id.btn_video_record % 65536)) {
                startActivity(new Intent(this, VideoRecordActivity.class));
            }
        } else if (v.getId() == R.id.btn_video_play) {
            startActivity(new Intent(this, VideoPlayActivity.class));
        } else if (v.getId() == R.id.btn_video_frame) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, VideoFrameActivity.class));
            }
        } else if (v.getId() == R.id.btn_camerax_photo) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.CAMERA}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, CameraxPhotoActivity.class));
            }
        } else if (v.getId() == R.id.btn_camerax_record) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, CameraxRecordActivity.class));
            }
        } else if (v.getId() == R.id.btn_exo_player) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, ExoPlayerActivity.class));
            }
        } else if (v.getId() == R.id.btn_short_view) {
            if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, (int) v.getId() % 65536)) {
                startActivity(new Intent(this, ShortViewActivity.class));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // requestCode?????????????????????????????????2???16?????????65536
        if (requestCode == R.id.btn_audio_play % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) {
                startActivity(new Intent(this, AudioPlayActivity.class));
            } else {
                Toast.makeText(this, "??????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_media_recorder % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) {
                startActivity(new Intent(this, MediaRecorderActivity.class));
            } else {
                Toast.makeText(this, "???????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_photo_take % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) {
                startActivity(new Intent(this, PhotoTakeActivity.class));
            } else {
                Toast.makeText(this, "??????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_video_record % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) {
                startActivity(new Intent(this, VideoRecordActivity.class));
            } else {
                Toast.makeText(this, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_video_frame % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // ???????????????????????????
                startActivity(new Intent(this, VideoFrameActivity.class));
            } else {
                Toast.makeText(this, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_camerax_photo % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // ???????????????????????????
                startActivity(new Intent(this, CameraxPhotoActivity.class));
            } else {
                Toast.makeText(this, "??????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_camerax_record % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // ???????????????????????????
                startActivity(new Intent(this, CameraxRecordActivity.class));
            } else {
                Toast.makeText(this, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_exo_player % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // ???????????????????????????
                startActivity(new Intent(this, ExoPlayerActivity.class));
            } else {
                Toast.makeText(this, "?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.btn_short_view % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // ???????????????????????????
                startActivity(new Intent(this, ShortViewActivity.class));
            } else {
                Toast.makeText(this, "?????????????????????????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
