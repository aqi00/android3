package com.example.chapter09;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.chapter09.adapter.DepartmentPagerAdapter;

public class DepartmentStoreActivity extends AppCompatActivity {
    private ViewPager vp_content; // 声明一个翻页视图对象
    private RadioGroup rg_tabbar; // 声明一个单选组对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_store);
        vp_content = findViewById(R.id.vp_content);
        // 构建一个翻页适配器
        DepartmentPagerAdapter adapter = new DepartmentPagerAdapter(getSupportFragmentManager());
        vp_content.setAdapter(adapter); // 设置翻页视图的适配器
        // 给翻页视图添加页面变更监听器
        vp_content.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // 选中指定位置的单选按钮
                rg_tabbar.check(rg_tabbar.getChildAt(position).getId());
            }
        });
        rg_tabbar = findViewById(R.id.rg_tabbar);
        // 设置单选组的选中监听器
        rg_tabbar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int pos=0; pos<rg_tabbar.getChildCount(); pos++) {
                    // 获得指定位置的单选按钮
                    RadioButton tab = (RadioButton) rg_tabbar.getChildAt(pos);
                    if (tab.getId() == checkedId) { // 正是当前选中的按钮
                        vp_content.setCurrentItem(pos); // 设置翻页视图显示第几页
                    }
                }
            }
        });
    }
}
