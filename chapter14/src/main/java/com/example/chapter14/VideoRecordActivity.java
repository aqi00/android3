package com.example.chapter14;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter14.util.DateUtil;
import com.example.chapter14.util.MediaUtil;

public class VideoRecordActivity extends AppCompatActivity {
    private final static String TAG = "VideoRecordActivity";
    private TextView tv_video;
    private RelativeLayout rl_video;
    private ImageView iv_video;
    private Uri mVideoUri; // 视频文件的路径对象
    private ActivityResultLauncher launcher; // 声明一个活动结果启动器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);
        tv_video = findViewById(R.id.tv_video);
        rl_video = findViewById(R.id.rl_video);
        iv_video = findViewById(R.id.iv_video);
        // 注册一个善后工作的活动结果启动器，准备打开摄像界面
        launcher = registerForActivityResult(
                new ActivityResultContracts.TakeVideo(), bitmap -> {
                    tv_video.setText("录制完成的视频地址为："+mVideoUri.toString());
                    Log.d(TAG, "mVideoUri="+mVideoUri.toString());
                    rl_video.setVisibility(View.VISIBLE);
                    if (bitmap == null) {
                        // 获取视频文件的某帧图片
                        bitmap = MediaUtil.getOneFrame(this, mVideoUri, 1000);
                    }
                    iv_video.setImageBitmap(bitmap);
                });
        findViewById(R.id.btn_recorder).setOnClickListener(v -> takeVideo());
        findViewById(R.id.rl_video).setOnClickListener(v -> {
            // 创建一个内容获取动作的意图（准备跳到系统播放器）
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(mVideoUri, "video/*"); // 类型为视频
            startActivity(intent); // 打开系统的视频播放器
        });
    }

    private void takeVideo() {
        // Android10开始必须由系统自动分配路径，同时该方式也能自动刷新相册
        ContentValues values = new ContentValues();
        // 指定图片文件的名称
        values.put(MediaStore.Video.Media.DISPLAY_NAME, "video_"+DateUtil.getNowDateTime());
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4"); // 类型为视频
        // 通过内容解析器插入一条外部内容的路径信息
        mVideoUri = getContentResolver().insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        launcher.launch(mVideoUri);
    }

}
