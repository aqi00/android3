package com.example.chapter16;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("DefaultLocale")
public class SensorActivity extends AppCompatActivity {
    private TextView tv_sensor; // 声明一个文本视图对象
    private String[] mSensorType = {
            "加速度", "磁场", "方向", "陀螺仪", "光线",
            "压力", "温度", "距离", "重力", "线性加速度",
            "旋转矢量", "湿度", "环境温度", "无标定磁场", "无标定旋转矢量",
            "未校准陀螺仪", "特殊动作", "步行检测", "计步器", "地磁旋转矢量",
            "心跳速率", "倾斜检测", "唤醒手势", "掠过手势", "拾起手势",
            "手腕倾斜", "设备方向", "六自由度姿态", "静止检测", "运动检测",
            "心跳检测", "动态元事件", "未知", "低延迟离体检测", "低延迟体外检测"};
    private Map<Integer, String> mapSensor = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        tv_sensor = findViewById(R.id.tv_sensor);
        showSensorInfo(); // 显示手机自带的传感器信息
    }

    // 显示手机自带的传感器信息
    private void showSensorInfo() {
        // 从系统服务中获取传感管理器对象
        SensorManager mSensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 获取当前设备支持的传感器列表
        List<Sensor> sensorList = mSensorMgr.getSensorList(Sensor.TYPE_ALL);
        String show_content = "当前支持的传感器包括：\n";
        for (Sensor sensor : sensorList) {
            if (sensor.getType() >= mSensorType.length) {
                continue;
            }
            mapSensor.put(sensor.getType(), sensor.getName());
        }
        for (Map.Entry<Integer, String> map : mapSensor.entrySet()) {
            int type = map.getKey();
            String name = map.getValue();
            String content = String.format("%d %s：%s\n", type, mSensorType[type - 1], name);
            show_content += content;
        }
        tv_sensor.setText(show_content);
    }

}
