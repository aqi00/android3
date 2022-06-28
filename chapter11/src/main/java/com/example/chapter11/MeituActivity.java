package com.example.chapter11;

import com.example.chapter11.util.BitmapUtil;
import com.example.chapter11.util.DateUtil;
import com.example.chapter11.widget.BitmapView;
import com.example.chapter11.widget.MeituView;
import com.example.chapter11.widget.MeituView.ImageChangetListener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MeituActivity extends AppCompatActivity implements ImageChangetListener {
    private final static String TAG = "MeituActivity";
    private int CHOOSE_CODE = 3; // 只在相册挑选图片的请求码
    private MeituView mv_content; // 声明一个美图视图对象
    private BitmapView bv_content; // 声明一个位图视图对象
    private TextView tv_hint; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meitu);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕常亮
        initView(); // 初始化视图
    }

    // 初始化视图
    private void initView() {
        Toolbar tl_head = findViewById(R.id.tl_head);
        tl_head.setTitle("抠图工具");
        setSupportActionBar(tl_head); // 替换系统自带的ActionBar
        // 设置工具栏左侧导航图标的点击监听器
        tl_head.setNavigationOnClickListener(view -> finish());
        mv_content = findViewById(R.id.mv_content);
        mv_content.setImageChangetListener(this); // 设置美图视图的图像变更监听器
        bv_content = findViewById(R.id.bv_content);
        bv_content.setDrawingCacheEnabled(true); // 开启位图视图的绘图缓存
        tv_hint = findViewById(R.id.tv_hint);
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

    // 在创建选项菜单时调用
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meitu, menu);
        return true;
    }

    // 在选中菜单项时调用
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_file_open) { // 点击了“打开文件”
            // 创建一个内容获取动作的意图（准备跳到系统相册）
            Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
            albumIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false); // 是否允许多选
            albumIntent.setType("image/*"); // 类型为图像
            startActivityForResult(albumIntent, CHOOSE_CODE); // 打开系统相册
        } else if (item.getItemId() == R.id.menu_file_save) { // 点击了“保存文件”
            Bitmap bitmap = mv_content.getCropBitmap(); // 获取美图视图处理后的位图
            // 生成图片文件的保存路径
            String path = String.format("%s/%s.jpg",
                    getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),
                    DateUtil.getNowDateTime());
            BitmapUtil.saveImage(path, bitmap); // 把位图保存为图片文件
            BitmapUtil.notifyPhotoAlbum(this, path); // 通知相册来了张新图片
            Toast.makeText(this, "已保存抠好的图片 "+path, Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == CHOOSE_CODE) { // 从相册返回
            if (intent.getData() != null) { // 从相册选择一张照片
                Uri uri = intent.getData(); // 获得已选择照片的路径对象
                // 根据指定图片的uri，获得自动缩小后的位图对象
                Bitmap bitmap = BitmapUtil.getAutoZoomImage(this, uri);
                bv_content.setImageBitmap(bitmap); // 设置位图视图的位图对象
                refreshImage(true); // 刷新图像展示
                tv_hint.setVisibility(View.GONE);
            }
        }
    }

}
