package com.example.chapter07;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter07.adapter.PlanetBaseAdapter;
import com.example.chapter07.bean.Planet;

import java.util.List;

public class BaseAdapterActivity extends AppCompatActivity {
    private List<Planet> planetList; // 声明一个行星列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_adapter);
        initPlanetSpinner(); // 初始化行星列表的下拉框
    }

    // 初始化行星列表的下拉框
    private void initPlanetSpinner() {
        // 获取默认的行星列表，即水星、金星、地球、火星、木星、土星
        planetList = Planet.getDefaultList();
        // 构建一个行星列表的适配器
        PlanetBaseAdapter adapter = new PlanetBaseAdapter(this, planetList);
        // 从布局文件中获取名叫sp_planet的下拉框
        Spinner sp_planet = findViewById(R.id.sp_planet);
        sp_planet.setPrompt("请选择行星"); // 设置下拉框的标题
        sp_planet.setAdapter(adapter); // 设置下拉框的列表适配器
        sp_planet.setSelection(0); // 设置下拉框默认显示第一项
        // 给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sp_planet.setOnItemSelectedListener(new MySelectedListener());
    }

    // 定义一个选择监听器，它实现了接口OnItemSelectedListener
    private class MySelectedListener implements OnItemSelectedListener {
        // 选择事件的处理方法，其中arg2代表选择项的序号
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            Toast.makeText(BaseAdapterActivity.this, "您选择的是" + planetList.get(arg2).name, Toast.LENGTH_LONG).show();
        }

        // 未选择时的处理方法，通常无需关注
        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}
