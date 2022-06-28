package com.example.chapter09;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_tab_navigation).setOnClickListener(this);
        findViewById(R.id.btn_tab_button).setOnClickListener(this);
        findViewById(R.id.btn_tab_pager).setOnClickListener(this);
        findViewById(R.id.btn_toolbar).setOnClickListener(this);
        findViewById(R.id.btn_overflow_menu).setOnClickListener(this);
        findViewById(R.id.btn_tab_layout).setOnClickListener(this);
        findViewById(R.id.btn_recycler_linear).setOnClickListener(this);
        findViewById(R.id.btn_recycler_grid).setOnClickListener(this);
        findViewById(R.id.btn_recycler_combine).setOnClickListener(this);
        findViewById(R.id.btn_recycler_staggered).setOnClickListener(this);
        findViewById(R.id.btn_recycler_dynamic).setOnClickListener(this);
        findViewById(R.id.btn_swipe_recycler).setOnClickListener(this);
        findViewById(R.id.btn_pager2_recycler).setOnClickListener(this);
        findViewById(R.id.btn_pager2_fragment).setOnClickListener(this);
        findViewById(R.id.btn_department_store).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_tab_navigation) {
            Intent intent = new Intent(this, TabNavigationActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_tab_button) {
            Intent intent = new Intent(this, TabButtonActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_tab_pager) {
            Intent intent = new Intent(this, TabPagerActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_toolbar) {
            Intent intent = new Intent(this, ToolbarActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_overflow_menu) {
            Intent intent = new Intent(this, OverflowMenuActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_tab_layout) {
            Intent intent = new Intent(this, TabLayoutActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_recycler_linear) {
            Intent intent = new Intent(this, RecyclerLinearActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_recycler_grid) {
            Intent intent = new Intent(this, RecyclerGridActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_recycler_combine) {
            Intent intent = new Intent(this, RecyclerCombineActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_recycler_staggered) {
            Intent intent = new Intent(this, RecyclerStaggeredActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_recycler_dynamic) {
            Intent intent = new Intent(this, RecyclerDynamicActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_swipe_recycler) {
            Intent intent = new Intent(this, SwipeRecyclerActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_pager2_recycler) {
            Intent intent = new Intent(this, ViewPager2RecyclerActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_pager2_fragment) {
            Intent intent = new Intent(this, ViewPager2FragmentActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_department_store) {
            Intent intent = new Intent(this, DepartmentStoreActivity.class);
            startActivity(intent);
        }
    }

}
