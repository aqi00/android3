package com.example.chapter12;

import android.graphics.ImageDecoder;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

@RequiresApi(api = Build.VERSION_CODES.P)
public class BezierCurveActivity extends AppCompatActivity {
    private ImageView iv_curve; // 声明一个图像视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bezier_curve);
        iv_curve = findViewById(R.id.iv_curve);
        initCurveSpinner(); // 初始化曲线类型下拉框
    }

    private String[] mCurveArray = {"一次贝塞尔曲线", "二次贝塞尔曲线", "三次贝塞尔曲线" };
    private int[] mDrawableArray = {R.drawable.bezier_first_order, R.drawable.bezier_second_order, R.drawable.bezier_third_order };
    // 初始化曲线类型下拉框
    private void initCurveSpinner() {
        ArrayAdapter<String> curveAdapter = new ArrayAdapter<>(this,
                R.layout.item_select, mCurveArray);
        Spinner sp_curve = findViewById(R.id.sp_curve);
        sp_curve.setPrompt("请选择曲线类型");
        sp_curve.setAdapter(curveAdapter);
        sp_curve.setOnItemSelectedListener(new CurveSelectedListener());
        sp_curve.setSelection(0);
    }

    private class CurveSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            showBezierCurve(arg2); // 显示贝塞尔曲线的演示动图
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 显示贝塞尔曲线的演示动图
    private void showBezierCurve(int order) {
        int drawableId = mDrawableArray[order];
        try {
            // 利用Android 9.0新增的ImageDecoder读取图片
            ImageDecoder.Source source = ImageDecoder.createSource(getResources(), drawableId);
            // 从数据源解码得到图形信息
            Drawable drawable = ImageDecoder.decodeDrawable(source);
            iv_curve.setImageDrawable(drawable); // 设置图像视图的图形对象
            if (drawable instanceof Animatable) { // 如果是动画图形，则开始播放动画
                ((Animatable) iv_curve.getDrawable()).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}