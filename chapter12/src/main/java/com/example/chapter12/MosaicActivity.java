package com.example.chapter12;

import com.example.chapter12.util.Utils;
import com.example.chapter12.widget.MosaicView;

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

public class MosaicActivity extends AppCompatActivity {
    private MosaicView mv_mosaic; // 声明一个马赛克视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mosaic);
        initView(); // 初始化视图
        initMosaicSpinner(); // 初始化马赛克类型下拉框
    }

    // 初始化视图
    private void initView() {
        mv_mosaic = findViewById(R.id.mv_mosaic);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bj08);
        mv_mosaic.setImageBitmap(bitmap); // 设置马赛克视图的位图对象
        ViewGroup.LayoutParams params = mv_mosaic.getLayoutParams();
        params.height = Utils.getScreenWidth(this) * bitmap.getHeight() / bitmap.getWidth();
        mv_mosaic.setLayoutParams(params); // 设置马赛克视图的布局参数
    }

    // 初始化马赛克类型下拉框
    private void initMosaicSpinner() {
        ArrayAdapter<String> mosaicAdapter = new ArrayAdapter<>(this,
                R.layout.item_select, mosaicArray);
        Spinner sp_mosaic = findViewById(R.id.sp_mosaic);
        sp_mosaic.setPrompt("请选择马赛克动画类型");
        sp_mosaic.setAdapter(mosaicAdapter);
        sp_mosaic.setOnItemSelectedListener(new MosaicSelectedListener());
        sp_mosaic.setSelection(0);
    }

    private String[] mosaicArray = {"水平二十格", "水平三十格", "水平四十格",
            "垂直二十格", "垂直三十格", "垂直四十格"};
    class MosaicSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            // 设置马赛克视图的动画方向
            mv_mosaic.setOriention((arg2 < 3) ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);
            if (arg2 == 0 || arg2 == 3) {
                mv_mosaic.setGridCount(20); // 设置马赛克的格子数量
            } else if (arg2 == 1 || arg2 == 4) {
                mv_mosaic.setGridCount(30); // 设置马赛克的格子数量
            } else if (arg2 == 2 || arg2 == 5) {
                mv_mosaic.setGridCount(40); // 设置马赛克的格子数量
            }
            // 起始值和结束值要超出一些范围，这样头尾的马赛克看起来才是连贯的
            int offset = 5;
            mv_mosaic.setOffset(offset); // 设置偏差比例
            // 构造一个按比率逐步展开的属性动画
            ObjectAnimator anim = ObjectAnimator.ofInt(mv_mosaic, "ratio", 0 - offset, 101 + offset);
            anim.setDuration(3000); // 设置动画的播放时长
            anim.start(); // 开始播放属性动画
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}
