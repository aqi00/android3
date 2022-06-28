package com.example.chapter07;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_spinner_dropdown).setOnClickListener(this);
        findViewById(R.id.btn_spinner_dialog).setOnClickListener(this);
        findViewById(R.id.btn_spinner_icon).setOnClickListener(this);
        findViewById(R.id.btn_base_adapter).setOnClickListener(this);
        findViewById(R.id.btn_list_view).setOnClickListener(this);
        findViewById(R.id.btn_list_focus).setOnClickListener(this);
        findViewById(R.id.btn_list_cart).setOnClickListener(this);
        findViewById(R.id.btn_grid_view).setOnClickListener(this);
        findViewById(R.id.btn_grid_channel).setOnClickListener(this);
        findViewById(R.id.btn_view_pager).setOnClickListener(this);
        findViewById(R.id.btn_tab_strip).setOnClickListener(this);
        findViewById(R.id.btn_launch_simple).setOnClickListener(this);
        findViewById(R.id.btn_fragment_static).setOnClickListener(this);
        findViewById(R.id.btn_fragment_dynamic).setOnClickListener(this);
        findViewById(R.id.btn_launch_improve).setOnClickListener(this);
        findViewById(R.id.btn_book_keeping).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_spinner_dropdown) {
            Intent intent = new Intent(this, SpinnerDropdownActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_spinner_dialog) {
            Intent intent = new Intent(this, SpinnerDialogActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_spinner_icon) {
            Intent intent = new Intent(this, SpinnerIconActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_base_adapter) {
            Intent intent = new Intent(this, BaseAdapterActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_list_view) {
            Intent intent = new Intent(this, ListViewActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_list_focus) {
            Intent intent = new Intent(this, ListFocusActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_list_cart) {
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_grid_view) {
            Intent intent = new Intent(this, GridViewActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_grid_channel) {
            Intent intent = new Intent(this, ShoppingChannelActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_view_pager) {
            Intent intent = new Intent(this, ViewPagerActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_tab_strip) {
            Intent intent = new Intent(this, PagerTabActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_launch_simple) {
            Intent intent = new Intent(this, LaunchSimpleActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_fragment_static) {
            Intent intent = new Intent(this, FragmentStaticActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_fragment_dynamic) {
            Intent intent = new Intent(this, FragmentDynamicActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_launch_improve) {
            Intent intent = new Intent(this, LaunchImproveActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_book_keeping) {
            Intent intent = new Intent(this, BillAddActivity.class);
            startActivity(intent);
        }
    }

}
