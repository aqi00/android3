package com.example.chapter11;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint("SetTextI18n")
public class DrawerLayoutActivity extends AppCompatActivity {
    private final static String TAG = "DrawerLayoutActivity";
    private DrawerLayout dl_layout; // 声明一个抽屉布局对象
    private Button btn_drawer_left; // 声明一个按钮对象
    private Button btn_drawer_right; // 声明一个按钮对象
    private TextView tv_drawer_center; // 声明一个文本视图对象
    private ListView lv_drawer_left; // 声明左侧菜单的列表视图对象
    private ListView lv_drawer_right; // 声明右侧菜单的列表视图对象
    private String[] titleArray = {"首页", "新闻", "娱乐", "博客", "论坛"}; // 左侧菜单项的标题数组
    private String[] settingArray = {"我的", "设置", "关于"}; // 右侧菜单项的标题数组

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_layout);
        dl_layout = findViewById(R.id.dl_layout);
        dl_layout.addDrawerListener(new SlidingListener()); // 设置侧滑监听器
        btn_drawer_left = findViewById(R.id.btn_drawer_left);
        btn_drawer_right = findViewById(R.id.btn_drawer_right);
        tv_drawer_center = findViewById(R.id.tv_drawer_center);
        btn_drawer_left.setOnClickListener(v -> {
            if (dl_layout.isDrawerOpen(lv_drawer_left)) { // 左侧菜单已打开
                dl_layout.closeDrawer(lv_drawer_left); // 关闭左侧抽屉
            } else { // 左侧菜单未打开
                dl_layout.openDrawer(lv_drawer_left); // 打开左侧抽屉
            }
        });
        btn_drawer_right.setOnClickListener(v -> {
            if (dl_layout.isDrawerOpen(lv_drawer_right)) { // 右侧菜单已打开
                dl_layout.closeDrawer(lv_drawer_right); // 关闭右侧抽屉
            } else { // 右侧菜单未打开
                dl_layout.openDrawer(lv_drawer_right); // 打开右侧抽屉
            }
        });
        initListDrawer(); // 初始化侧滑的菜单列表
    }

    // 初始化侧滑的菜单列表
    private void initListDrawer() {
        // 下面初始化左侧菜单的列表视图
        lv_drawer_left = findViewById(R.id.lv_drawer_left);
        ArrayAdapter<String> left_adapter = new ArrayAdapter<>(this,
                R.layout.item_select, titleArray);
        lv_drawer_left.setAdapter(left_adapter);
        lv_drawer_left.setOnItemClickListener((parent, view, position, id) -> {
            String text = titleArray[position];
            tv_drawer_center.setText("这里是" + text + "页面");
            dl_layout.closeDrawers(); // 关闭所有抽屉
        });
        // 下面初始化右侧菜单的列表视图
        lv_drawer_right = findViewById(R.id.lv_drawer_right);
        ArrayAdapter<String> right_adapter = new ArrayAdapter<>(this,
                R.layout.item_select, settingArray);
        lv_drawer_right.setAdapter(right_adapter);
        lv_drawer_right.setOnItemClickListener((parent, view, position, id) -> {
            String text = settingArray[position];
            tv_drawer_center.setText("这里是" + text + "页面");
            dl_layout.closeDrawers(); // 关闭所有抽屉
        });
    }

    // 定义一个抽屉布局的侧滑监听器
    private class SlidingListener implements DrawerListener {
        // 在拉出抽屉的过程中触发
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {}

        // 在侧滑抽屉打开后触发
        @Override
        public void onDrawerOpened(View drawerView) {
            if (drawerView.getId() == R.id.lv_drawer_left) {
                btn_drawer_left.setText("关闭左边侧滑");
            } else {
                btn_drawer_right.setText("关闭右边侧滑");
            }
        }

        // 在侧滑抽屉关闭后触发
        @Override
        public void onDrawerClosed(View drawerView) {
            if (drawerView.getId() == R.id.lv_drawer_left) {
                btn_drawer_left.setText("打开左边侧滑");
            } else {
                btn_drawer_right.setText("打开右边侧滑");
            }
        }

        // 在侧滑状态变更时触发
        @Override
        public void onDrawerStateChanged(int paramInt) {}
    }

}
