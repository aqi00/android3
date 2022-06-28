package com.example.chapter12;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.chapter12.widget.LayerView;

public class DrawLayerActivity extends AppCompatActivity {
    private LayerView lv_mode; // 声明一个层次视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_layer);
        lv_mode = findViewById(R.id.lv_mode);
        initLayerSpinner(); // 初始化绘图层次类型下拉框
    }

    // 初始化绘图层次类型下拉框
    private void initLayerSpinner() {
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(this,
                R.layout.item_select, descArray);
        Spinner sp_mode = findViewById(R.id.sp_mode);
        sp_mode.setPrompt("请选择绘图层次类型");
        sp_mode.setAdapter(modeAdapter);
        sp_mode.setOnItemSelectedListener(new ModeSelectedListener());
        sp_mode.setSelection(0);
    }

    private String[] descArray = {
            "只显示轮廓", "SRC 只显示上层图形", "DST 只显示下层图形",
            "SRC_OVER 重叠部分由上层遮盖下层", "DST_OVER 重叠部分由下层遮盖上层",
            "SRC_IN 只显示重叠部分的上层图形", "DST_IN 只显示重叠部分的下层图形",
            "SRC_OUT 只显示上层图形的未重叠部分", "DST_OUT 只显示下层图形的未重叠部分",
            "SRC_ATOP 只显示上层图形区域，但重叠部分显示下层图形", "DST_ATOP 只显示下层图形区域，但重叠部分显示上层图形",
            "XOR 不显示重叠部分，其余部分正常显示", "DARKEN 重叠部分按颜料混合方式加深，其余部分正常显示",
            "LIGHTEN 重叠部分按光照重合方式加亮，其余部分正常显示", "MULTIPLY 只显示重叠部分，且重叠部分的颜色混合加深",
            "SCREEN 过滤重叠部分的深色，其余部分正常显示"};
    private PorterDuff.Mode[] modeArray = {
            PorterDuff.Mode.CLEAR, PorterDuff.Mode.SRC, PorterDuff.Mode.DST,
            PorterDuff.Mode.SRC_OVER, PorterDuff.Mode.DST_OVER,
            PorterDuff.Mode.SRC_IN, PorterDuff.Mode.DST_IN,
            PorterDuff.Mode.SRC_OUT, PorterDuff.Mode.DST_OUT,
            PorterDuff.Mode.SRC_ATOP, PorterDuff.Mode.DST_ATOP,
            PorterDuff.Mode.XOR, PorterDuff.Mode.DARKEN,
            PorterDuff.Mode.LIGHTEN, PorterDuff.Mode.MULTIPLY,
            PorterDuff.Mode.SCREEN};
    class ModeSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (arg2 == 0) {
                lv_mode.setOnlyLine(); // 只显示线条轮廓
            } else {
                lv_mode.setMode(modeArray[arg2]); // 设置层次视图的绘图模式
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}