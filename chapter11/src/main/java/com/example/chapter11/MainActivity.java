package com.example.chapter11;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_key_soft).setOnClickListener(this);
        findViewById(R.id.btn_key_hard).setOnClickListener(this);
        findViewById(R.id.btn_back_press).setOnClickListener(this);
        findViewById(R.id.btn_event_dispatch).setOnClickListener(this);
        findViewById(R.id.btn_event_intercept).setOnClickListener(this);
        findViewById(R.id.btn_touch_single).setOnClickListener(this);
        findViewById(R.id.btn_touch_multiple).setOnClickListener(this);
        findViewById(R.id.btn_signature).setOnClickListener(this);
        findViewById(R.id.btn_click_long).setOnClickListener(this);
        findViewById(R.id.btn_slide_direction).setOnClickListener(this);
        findViewById(R.id.btn_scale_rotate).setOnClickListener(this);
        findViewById(R.id.btn_custom_scroll).setOnClickListener(this);
        findViewById(R.id.btn_disallow_scroll).setOnClickListener(this);
        findViewById(R.id.btn_drawer_layout).setOnClickListener(this);
        findViewById(R.id.btn_pull_refresh).setOnClickListener(this);
        findViewById(R.id.btn_meitu).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_key_soft) {
            startActivity(new Intent(this, KeySoftActivity.class));
        } else if (v.getId() == R.id.btn_key_hard) {
            startActivity(new Intent(this, KeyHardActivity.class));
        } else if (v.getId() == R.id.btn_back_press) {
            startActivity(new Intent(this, BackPressActivity.class));
        } else if (v.getId() == R.id.btn_event_dispatch) {
            startActivity(new Intent(this, EventDispatchActivity.class));
        } else if (v.getId() == R.id.btn_event_intercept) {
            startActivity(new Intent(this, EventInterceptActivity.class));
        } else if (v.getId() == R.id.btn_touch_single) {
            startActivity(new Intent(this, TouchSingleActivity.class));
        } else if (v.getId() == R.id.btn_touch_multiple) {
            startActivity(new Intent(this, TouchMultipleActivity.class));
        } else if (v.getId() == R.id.btn_signature) {
            startActivity(new Intent(this, SignatureActivity.class));
        } else if (v.getId() == R.id.btn_click_long) {
            startActivity(new Intent(this, ClickLongActivity.class));
        } else if (v.getId() == R.id.btn_slide_direction) {
            startActivity(new Intent(this, SlideDirectionActivity.class));
        } else if (v.getId() == R.id.btn_scale_rotate) {
            startActivity(new Intent(this, ScaleRotateActivity.class));
        } else if (v.getId() == R.id.btn_custom_scroll) {
            startActivity(new Intent(this, CustomScrollActivity.class));
        } else if (v.getId() == R.id.btn_disallow_scroll) {
            startActivity(new Intent(this, DisallowScrollActivity.class));
        } else if (v.getId() == R.id.btn_drawer_layout) {
            startActivity(new Intent(this, DrawerLayoutActivity.class));
        } else if (v.getId() == R.id.btn_pull_refresh) {
            startActivity(new Intent(this, PullRefreshActivity.class));
        } else if (v.getId() == R.id.btn_meitu) {
            startActivity(new Intent(this, MeituActivity.class));
        }
    }

}
