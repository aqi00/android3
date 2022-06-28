package com.example.chapter07;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.chapter07.adapter.PlanetListWithButtonAdapter;
import com.example.chapter07.bean.Planet;

import java.util.List;

public class ListFocusActivity extends AppCompatActivity {
    private final static String TAG = "ListFocusActivity";
    private ListView lv_planet; // 声明一个列表视图对象
    private PlanetListWithButtonAdapter adapter; // 行星列表的列表适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_focus);
        initFocusSpinner(); // 初始化焦点抢占方式的下拉框
    }

    // 初始化焦点抢占方式的下拉框
    private void initFocusSpinner() {
        ArrayAdapter<String> focusAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, focusArray);
        Spinner sp_focus = findViewById(R.id.sp_focus);
        sp_focus.setPrompt("请选择焦点抢占方式"); // 设置下拉框的标题
        sp_focus.setAdapter(focusAdapter); // 设置下拉框的数组适配器
        sp_focus.setSelection(0); // 设置下拉框默认显示第一项
        // 给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sp_focus.setOnItemSelectedListener(new FocusSelectedListener());
    }

    private String[] focusArray = {
            "在子控件之前处理",
            "在子控件之后处理",
            "不让子控件处理",
    };

    class FocusSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (arg2 == 0) {  // 在子控件之前处理
                showListView(ViewGroup.FOCUS_BEFORE_DESCENDANTS); // 显示指定抢占方式的列表视图
            } else if (arg2 == 1) {  // 在子控件之后处理
                showListView(ViewGroup.FOCUS_AFTER_DESCENDANTS); // 显示指定抢占方式的列表视图
            } else if (arg2 == 2) {  // 不让子控件处理，此时才会响应列表项的点击和长按事件
                showListView(ViewGroup.FOCUS_BLOCK_DESCENDANTS); // 显示指定抢占方式的列表视图
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    // 显示指定抢占方式的列表视图
    private void showListView(int focusMethod) {
        List<Planet> planetList = Planet.getDefaultList(); // 获得默认的行星列表
        // 构建一个行星列表的列表适配器
        adapter = new PlanetListWithButtonAdapter(this, planetList, focusMethod);
        // 从布局视图中获取名叫lv_planet的列表视图
        lv_planet = findViewById(R.id.lv_planet);
        lv_planet.setAdapter(adapter); // 设置列表视图的适配器
        lv_planet.setOnItemClickListener(adapter); // 设置列表视图的点击监听器
        lv_planet.setOnItemLongClickListener(adapter); // 设置列表视图的长按监听器
    }

}
