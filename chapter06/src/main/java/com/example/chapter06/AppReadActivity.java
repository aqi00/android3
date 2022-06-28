package com.example.chapter06;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class AppReadActivity extends AppCompatActivity {
	private TextView tv_app; // 声明一个文本视图对象
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_read);
		tv_app = findViewById(R.id.tv_app);
		readAppMemory(); // 读取全局内存中保存的变量信息
	}

	// 读取全局内存中保存的变量信息
	private void readAppMemory() {
		String desc = "全局内存中保存的信息如下：";
		// 获取当前应用的Application实例
		MainApplication app = MainApplication.getInstance();
		// 获取Application实例中保存的映射全局变量
		Map<String, String> mapParam = app.infoMap;
		// 遍历映射全局变量内部的键值对信息
		for (Map.Entry<String, String> item_map : mapParam.entrySet()) {
			desc = String.format("%s\n　%s的取值为%s",
					desc, item_map.getKey(), item_map.getValue());
		}
		if (mapParam.size() <= 0) {
			desc = "全局内存中保存的信息为空";
		}
		tv_app.setText(desc);
	}
	
}
