package com.example.chapter16;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter16.util.DateUtil;

@SuppressLint("SetTextI18n")
public class AccelerationActivity extends AppCompatActivity implements SensorEventListener {
    private TextView tv_shake; // 声明一个文本视图对象
    private SensorManager mSensorMgr; // 声明一个传感管理器对象
    private Vibrator mVibrator; // 声明一个震动器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceleration);
        tv_shake = findViewById(R.id.tv_shake);
        // 从系统服务中获取传感管理器对象
        mSensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 从系统服务中获取震动器对象
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorMgr.unregisterListener(this); // 注销当前活动的传感监听器
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 给加速度传感器注册传感监听器
        mSensorMgr.registerListener(this,
                mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { // 加速度变更事件
            // values[0]:X轴，values[1]：Y轴，values[2]：Z轴
            float[] values = event.values;
            if ((Math.abs(values[0]) > 15 || Math.abs(values[1]) > 15
                    || Math.abs(values[2]) > 15)) {
                tv_shake.setText(DateUtil.getNowFullDateTime() + " 我看到你摇一摇啦");
                mVibrator.vibrate(500); // 系统检测到摇一摇事件后，震动手机提示用户
            }
        }
    }

    // 当传感器精度改变时回调该方法，一般无需处理
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}
