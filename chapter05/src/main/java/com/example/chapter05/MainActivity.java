package com.example.chapter05;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_shape).setOnClickListener(this);
        findViewById(R.id.btn_nine).setOnClickListener(this);
        findViewById(R.id.btn_state).setOnClickListener(this);
        findViewById(R.id.btn_checkbox).setOnClickListener(this);
        findViewById(R.id.btn_switch_default).setOnClickListener(this);
        findViewById(R.id.btn_switch_ios).setOnClickListener(this);
        findViewById(R.id.btn_radio_horizontal).setOnClickListener(this);
        findViewById(R.id.btn_radio_vertical).setOnClickListener(this);
        findViewById(R.id.btn_edit_simple).setOnClickListener(this);
        findViewById(R.id.btn_edit_border).setOnClickListener(this);
        findViewById(R.id.btn_edit_focus).setOnClickListener(this);
        findViewById(R.id.btn_edit_hide).setOnClickListener(this);
        findViewById(R.id.btn_alert).setOnClickListener(this);
        findViewById(R.id.btn_date_picker).setOnClickListener(this);
        findViewById(R.id.btn_time_picker).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_shape) {
            Intent intent = new Intent(this, DrawableShapeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_nine) {
            Intent intent = new Intent(this, DrawableNineActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_state) {
            Intent intent = new Intent(this, DrawableStateActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_checkbox) {
            Intent intent = new Intent(this, CheckBoxActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_switch_default) {
            Intent intent = new Intent(this, SwitchDefaultActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_switch_ios) {
            Intent intent = new Intent(this, SwitchIOSActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_radio_horizontal) {
            Intent intent = new Intent(this, RadioHorizontalActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_radio_vertical) {
            Intent intent = new Intent(this, RadioVerticalActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_edit_simple) {
            Intent intent = new Intent(this, EditSimpleActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_edit_border) {
            Intent intent = new Intent(this, EditBorderActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_edit_focus) {
            Intent intent = new Intent(this, EditFocusActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_edit_hide) {
            Intent intent = new Intent(this, EditHideActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_alert) {
            Intent intent = new Intent(this, AlertDialogActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_date_picker) {
            Intent intent = new Intent(this, DatePickerActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_time_picker) {
            Intent intent = new Intent(this, TimePickerActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_login) {
            Intent intent = new Intent(this, LoginMainActivity.class);
            startActivity(intent);
        }
    }

}
