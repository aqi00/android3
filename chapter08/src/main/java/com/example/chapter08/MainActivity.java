package com.example.chapter08;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_custom_button).setOnClickListener(this);
        findViewById(R.id.btn_measure_text).setOnClickListener(this);
        findViewById(R.id.btn_measure_layout).setOnClickListener(this);
        findViewById(R.id.btn_show_draw).setOnClickListener(this);
        findViewById(R.id.btn_month_picker).setOnClickListener(this);
        findViewById(R.id.btn_custom_tab).setOnClickListener(this);
        findViewById(R.id.btn_noscroll_list).setOnClickListener(this);
        findViewById(R.id.btn_notify_simple).setOnClickListener(this);
        findViewById(R.id.btn_notify_counter).setOnClickListener(this);
        findViewById(R.id.btn_notify_channel).setOnClickListener(this);
        findViewById(R.id.btn_foreground_service).setOnClickListener(this);
        findViewById(R.id.btn_float_notice).setOnClickListener(this);
        findViewById(R.id.btn_handler_post).setOnClickListener(this);
        findViewById(R.id.btn_view_invalidate).setOnClickListener(this);
        findViewById(R.id.btn_pie_animation).setOnClickListener(this);
        findViewById(R.id.btn_banner_pager).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_custom_button) {
            Intent intent = new Intent(this, CustomButtonActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_measure_text) {
            Intent intent = new Intent(this, MeasureTextActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_measure_layout) {
            Intent intent = new Intent(this, MeasureLayoutActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_show_draw) {
            Intent intent = new Intent(this, ShowDrawActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_month_picker) {
            Intent intent = new Intent(this, MonthPickerActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_custom_tab) {
            Intent intent = new Intent(this, CustomTabActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_noscroll_list) {
            Intent intent = new Intent(this, NoscrollListActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_notify_simple) {
            Intent intent = new Intent(this, NotifySimpleActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_notify_counter) {
            Intent intent = new Intent(this, NotifyCounterActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_notify_channel) {
            Intent intent = new Intent(this, NotifyChannelActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_foreground_service) {
            Intent intent = new Intent(this, ForegroundServiceActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_float_notice) {
            Intent intent = new Intent(this, FloatNoticeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_handler_post) {
            Intent intent = new Intent(this, HandlerPostActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_view_invalidate) {
            Intent intent = new Intent(this, ViewInvalidateActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_pie_animation) {
            Intent intent = new Intent(this, PieAnimationActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_banner_pager) {
            Intent intent = new Intent(this, BannerPagerActivity.class);
            startActivity(intent);
        }
    }

}
