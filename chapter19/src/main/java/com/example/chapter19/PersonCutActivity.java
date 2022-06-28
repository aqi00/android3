package com.example.chapter19;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter19.util.BitmapUtil;
import com.example.chapter19.util.DateUtil;
import com.example.chapter19.widget.BitmapView;
import com.example.chapter19.widget.MeituView;

public class PersonCutActivity extends AppCompatActivity implements MeituView.ImageChangetListener {
    private final static String TAG = "PersonCutActivity";
    private MeituView mv_content; // 声明一个美图视图对象
    private BitmapView bv_content; // 声明一个位图视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_cut);
        findViewById(R.id.iv_cancel).setOnClickListener(v -> finish());
        findViewById(R.id.iv_confirm).setOnClickListener(v -> saveFace());
        mv_content = findViewById(R.id.mv_content);
        mv_content.setImageChangetListener(this); // 设置美图视图的图像变更监听器
        bv_content = findViewById(R.id.bv_content);
        bv_content.setDrawingCacheEnabled(true); // 开启位图视图的绘图缓存
        String image_path = getIntent().getStringExtra("image_path");
        Log.d(TAG, "image_path="+image_path);
        bv_content.setImageBitmap(BitmapFactory.decodeFile(image_path)); // 设置位图视图的位图对象
        // 延迟200毫秒再刷新图像展示，因为开启绘图缓存需要时间
        new Handler(Looper.myLooper()).postDelayed(() -> refreshImage(true), 200);
    }

    // 保存人脸图片
    private void saveFace() {
        Bitmap bitmap = mv_content.getCropBitmap(); // 获取美图视图处理后的位图
        String face_path = String.format("%s/%s.jpg",
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),
                DateUtil.getNowDateTime());
        BitmapUtil.saveImage(face_path, bitmap); // 把位图保存为图片文件
        Intent intent = new Intent(); // 创建一个新意图
        intent.putExtra("face_path", face_path); // 把快递包裹塞给意图
        setResult(Activity.RESULT_OK, intent); // 携带意图返回上一个页面
        finish(); // 关闭当前页面
    }

    // 刷新图像展示
    private void refreshImage(boolean is_first) {
        Bitmap bitmap = bv_content.getDrawingCache(); // 从绘图缓存获取位图对象
        mv_content.setOrigBitmap(bitmap); // 设置美图视图的原始位图
        if (is_first) { // 首次打开
            int left = bitmap.getWidth() / 4;
            int top = bitmap.getHeight() / 4;
            // 设置美图视图的位图边界
            mv_content.setBitmapRect(new Rect(left, top, left * 2, top * 2));
        } else { // 非首次打开
            // 设置美图视图的位图边界
            mv_content.setBitmapRect(mv_content.getBitmapRect());
        }
    }

    // 在图片平移时触发
    @Override
    public void onImageTraslate(int offsetX, int offsetY, boolean bReset) {
        bv_content.setOffset(offsetX, offsetY, bReset); // 设置位图视图的偏移距离
        refreshImage(false); // 刷新图像展示
    }

    // 在图片缩放时触发
    @Override
    public void onImageScale(float ratio) {
        bv_content.setScaleRatio(ratio, false); // 设置位图视图的缩放比率
        refreshImage(false); // 刷新图像展示
    }

    // 在图片旋转时触发
    @Override
    public void onImageRotate(int degree) {
        bv_content.setRotateDegree(degree, false); // 设置位图视图的旋转角度
        refreshImage(false); // 刷新图像展示
    }

    // 在图片点击时触发
    @Override
    public void onImageClick() {}

    // 在图片长按时触发
    @Override
    public void onImageLongClick() {}

}