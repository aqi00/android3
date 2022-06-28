package com.example.chapter14;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter14.util.BitmapUtil;
import com.example.chapter14.util.DateUtil;

public class PhotoTakeActivity extends AppCompatActivity {
    private final static String TAG = "PhotoTakeActivity";
    private ImageView iv_photo; // 声明一个图像视图对象
    private Uri mImageUri; // 图片的路径对象
    private ActivityResultLauncher launcherThumbnail; // 声明一个活动结果启动器对象
    private ActivityResultLauncher launcherOriginal; // 声明一个活动结果启动器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_take);
        iv_photo = findViewById(R.id.iv_photo);
        // 注册一个善后工作的活动结果启动器，准备打开拍照界面（返回缩略图）
        launcherThumbnail = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(), bitmap -> iv_photo.setImageBitmap(bitmap));
        findViewById(R.id.btn_thumbnail).setOnClickListener(v -> launcherThumbnail.launch(null));
        // 注册一个善后工作的活动结果启动器，准备打开拍照界面（返回原始图）
        launcherOriginal = registerForActivityResult(
                new ActivityResultContracts.TakePicture(), result -> {
                    if (result) {
                        Bitmap bitmap = BitmapUtil.getAutoZoomImage(this, mImageUri);
                        iv_photo.setImageBitmap(bitmap); // 设置图像视图的位图对象
                    }
                });
        findViewById(R.id.btn_original).setOnClickListener(v -> takeOriginalPhoto());
    }

    // 拍照时获取原始图片
    private void takeOriginalPhoto() {
        // Android10开始必须由系统自动分配路径，同时该方式也能自动刷新相册
        ContentValues values = new ContentValues();
        // 指定图片文件的名称
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "photo_"+DateUtil.getNowDateTime());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg"); // 类型为图像
        // 通过内容解析器插入一条外部内容的路径信息
        mImageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        launcherOriginal.launch(mImageUri);
    }

}
