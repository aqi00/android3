package com.example.chapter09;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.RadioGroup;

import com.example.chapter09.adapter.MobileRecyclerAdapter;
import com.example.chapter09.bean.GoodsInfo;

public class ViewPager2RecyclerActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private ViewPager2 vp2_content; // 声明一个二代翻页视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager2_recycler);
        RadioGroup rg_orientation = findViewById(R.id.rg_orientation);
        rg_orientation.setOnCheckedChangeListener(this);
        // 从布局文件中获取名叫vp2_content的二代翻页视图
        vp2_content = findViewById(R.id.vp2_content);
        // 设置二代翻页视图的排列方向为水平方向
        vp2_content.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        // 构建一个商品信息列表的循环适配器
        MobileRecyclerAdapter adapter = new MobileRecyclerAdapter(this, GoodsInfo.getDefaultList());
        vp2_content.setAdapter(adapter); // 设置二代翻页视图的适配器

        // ViewPager2支持展示左右两页的部分区域
//        RecyclerView cv_content = (RecyclerView) vp2_content.getChildAt(0);
//        cv_content.setPadding(Utils.dip2px(this, 60), 0, Utils.dip2px(this, 60), 0);
//        cv_content.setClipToPadding(false); // false表示不裁剪下级视图

        // ViewPager2支持在翻页时展示切换动画，通过页面转换器计算切换动画的各项参数
//        ViewPager2.PageTransformer animator = new ViewPager2.PageTransformer() {
//            @Override
//            public void transformPage(@NonNull View page, float position) {
//                page.setRotation(position * 360); // 设置页面的旋转角度
//            }
//        };
//        vp2_content.setPageTransformer(animator); // 设置二代翻页视图的页面转换器
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb_horizontal) {
            // 设置二代翻页视图的排列方向为水平方向
            vp2_content.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        } else if (checkedId == R.id.rb_vertical) {
            // 设置二代翻页视图的排列方向为垂直方向
            vp2_content.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        }
    }
}
