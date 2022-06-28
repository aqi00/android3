package com.example.chapter12;

import com.example.chapter12.util.Utils;
import com.example.chapter12.widget.ShutterView;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class ShutterActivity extends AppCompatActivity {
    private ShutterView sv_shutter; // 声明一个百叶窗视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shutter);
        initView(); // 初始化视图
        initShutterSpinner(); // 初始化动画类型下拉框
    }

    // 初始化视图
    private void initView() {
        sv_shutter = findViewById(R.id.sv_shutter);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bj03);
        sv_shutter.setImageBitmap(bitmap); // 设置百叶窗视图的位图对象
        ViewGroup.LayoutParams params = sv_shutter.getLayoutParams();
        params.height = Utils.getScreenWidth(this) * bitmap.getHeight() / bitmap.getWidth();
        sv_shutter.setLayoutParams(params); // 设置百叶窗视图的布局参数
    }

    // 初始化动画类型下拉框
    private void initShutterSpinner() {
        ArrayAdapter<String> shutterAdapter = new ArrayAdapter<>(this,
                R.layout.item_select, shutterArray);
        Spinner sp_shutter = findViewById(R.id.sp_shutter);
        sp_shutter.setPrompt("请选择百叶窗动画类型");
        sp_shutter.setAdapter(shutterAdapter);
        sp_shutter.setOnItemSelectedListener(new ShutterSelectedListener());
        sp_shutter.setSelection(0);
    }

    private String[] shutterArray = {"水平五叶", "水平十叶", "水平二十叶",
            "垂直五叶", "垂直十叶", "垂直二十叶"};
    class ShutterSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // 设置百叶窗视图的动画方向
            sv_shutter.setOriention((arg2 < 3) ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
            if (arg2 == 0 || arg2 == 3) {
                sv_shutter.setLeafCount(5); // 设置百叶窗的叶片数量
            } else if (arg2 == 1 || arg2 == 4) {
                sv_shutter.setLeafCount(10); // 设置百叶窗的叶片数量
            } else if (arg2 == 2 || arg2 == 5) {
                sv_shutter.setLeafCount(20); // 设置百叶窗的叶片数量
            }
            // 构造一个按比率逐步展开的属性动画
            ObjectAnimator anim = ObjectAnimator.ofInt(sv_shutter, "ratio", 0, 100);
            anim.setDuration(3000); // 设置动画的播放时长
            anim.start(); // 开始播放属性动画
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}
