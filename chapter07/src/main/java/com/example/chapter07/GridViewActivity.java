package com.example.chapter07;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter07.adapter.PlanetGridAdapter;
import com.example.chapter07.bean.Planet;
import com.example.chapter07.util.Utils;

import java.util.List;

public class GridViewActivity extends AppCompatActivity {
    private final static String TAG = "GridViewActivity";
    private GridView gv_planet; // 声明一个网格视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        List<Planet> planetList = Planet.getDefaultList();
        // 构建一个行星列表的网格适配器
        PlanetGridAdapter adapter = new PlanetGridAdapter(this, planetList);
        // 从布局视图中获取名叫gv_planet的网格视图
        gv_planet = findViewById(R.id.gv_planet);
        gv_planet.setAdapter(adapter); // 设置网格视图的适配器
        gv_planet.setOnItemClickListener(adapter); // 设置网格视图的点击监听器
        gv_planet.setOnItemLongClickListener(adapter); // 设置网格视图的长按监听器
        initDividerSpinner(); // 初始化拉伸模式的下拉框
    }

    // 初始化拉伸模式的下拉框
    private void initDividerSpinner() {
        ArrayAdapter<String> dividerAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, dividerArray);
        Spinner sp_stretch = findViewById(R.id.sp_stretch);
        sp_stretch.setPrompt("请选择拉伸模式"); // 设置下拉框的标题
        sp_stretch.setAdapter(dividerAdapter); // 设置下拉框的数组适配器
        sp_stretch.setSelection(0); // 设置下拉框默认显示第一项
        // 给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sp_stretch.setOnItemSelectedListener(new DividerSelectedListener());
    }

    private String[] dividerArray = {
            "不显示分隔线",
            "不拉伸(NO_STRETCH)",
            "拉伸列宽(COLUMN_WIDTH)",
            "列间空隙(STRETCH_SPACING)",
            "左右空隙(SPACING_UNIFORM)",
            "使用padding显示全部分隔线"
    };
    class DividerSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            int dividerPad = Utils.dip2px(GridViewActivity.this, 2); // 定义间隔宽度为2dp
            gv_planet.setBackgroundColor(Color.CYAN);  // 设置背景颜色
            gv_planet.setHorizontalSpacing(dividerPad);  // 设置列表项在水平方向的间距
            gv_planet.setVerticalSpacing(dividerPad);  // 设置列表项在垂直方向的间距
            gv_planet.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);  // 设置拉伸模式
            gv_planet.setColumnWidth(Utils.dip2px(GridViewActivity.this, 120));  // 设置每列宽度为120dp
            gv_planet.setPadding(0, 0, 0, 0);  // 设置网格视图的四周间距
            if (arg2 == 0) {  // 不显示分隔线
                gv_planet.setBackgroundColor(Color.WHITE);
                gv_planet.setHorizontalSpacing(0);
                gv_planet.setVerticalSpacing(0);
            } else if (arg2 == 1) {  // 不拉伸(NO_STRETCH)
                gv_planet.setStretchMode(GridView.NO_STRETCH);
            } else if (arg2 == 2) {  // 拉伸列宽(COLUMN_WIDTH)
                gv_planet.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            } else if (arg2 == 3) {  // 列间空隙(STRETCH_SPACING)
                gv_planet.setStretchMode(GridView.STRETCH_SPACING);
            } else if (arg2 == 4) {  // 左右空隙(SPACING_UNIFORM)
                gv_planet.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
            } else if (arg2 == 5) {  // 使用padding显示全部分隔线
                gv_planet.setPadding(dividerPad, dividerPad, dividerPad, dividerPad);
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}
