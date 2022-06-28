package com.example.chapter07;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter07.adapter.PlanetListAdapter;
import com.example.chapter07.bean.Planet;
import com.example.chapter07.util.Utils;

import java.util.List;

public class ListViewActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private final static String TAG = "ListViewActivity";
    private CheckBox ck_divider; // 声明一个复选框对象
    private CheckBox ck_selector; // 声明一个复选框对象
    private ListView lv_planet; // 声明一个列表视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        ck_divider = findViewById(R.id.ck_divider);
        ck_selector = findViewById(R.id.ck_selector);
        ck_divider.setOnCheckedChangeListener(this);
        ck_selector.setOnCheckedChangeListener(this);
        List<Planet> planetList = Planet.getDefaultList(); // 获得默认的行星列表
        // 构建一个行星列表的列表适配器
        PlanetListAdapter adapter = new PlanetListAdapter(this, planetList);
        // 从布局视图中获取名叫lv_planet的列表视图
        lv_planet = findViewById(R.id.lv_planet);
        lv_planet.setAdapter(adapter); // 设置列表视图的适配器
        lv_planet.setOnItemClickListener(adapter); // 设置列表视图的点击监听器
        lv_planet.setOnItemLongClickListener(adapter); // 设置列表视图的长按监听器
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        refreshListView(); // 刷新列表视图
    }

    // 刷新列表视图
    private void refreshListView() {
//        lv_planet.setCacheColorHint(Color.TRANSPARENT); // 防止滚动时列表拉黑
        if (ck_divider.isChecked()) { // 显示分隔线
            // 从资源文件获得图形对象
            Drawable drawable = getResources().getDrawable(R.color.red);
            lv_planet.setDivider(drawable); // 设置列表视图的分隔线
            lv_planet.setDividerHeight(Utils.dip2px(this, 5)); // 设置列表视图的分隔线高度
        } else { // 不显示分隔线
            lv_planet.setDivider(null); // 设置列表视图的分隔线
            lv_planet.setDividerHeight(0); // 设置列表视图的分隔线高度
        }
        if (ck_selector.isChecked()) { // 显示按压背景
            lv_planet.setSelector(R.drawable.list_selector); // 设置列表项的按压状态图形
        } else { // 不显示按压背景
            //lv_planet.setSelector(null); // 直接设置null会报错，因为运行时报空指针异常
            // 从资源文件获得图形对象
            Drawable drawable = getResources().getDrawable(R.color.transparent);
            lv_planet.setSelector(drawable); // 设置列表项的按压状态图形
        }
    }

}
