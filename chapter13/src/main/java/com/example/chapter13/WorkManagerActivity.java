package com.example.chapter13;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.chapter13.util.DateUtil;
import com.example.chapter13.work.CollectWork;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class WorkManagerActivity extends AppCompatActivity {
    private final static String TAG = "WorkManagerActivity";
    private TextView tv_result; // 声明一个文本视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_manager);
        tv_result = findViewById(R.id.tv_result);
        findViewById(R.id.btn_once_start).setOnClickListener(v -> doOnceWork());
        findViewById(R.id.btn_period_start).setOnClickListener(v -> doPeriodWork());
    }

    // 执行一次性工作
    private void doOnceWork() {
        Log.d(TAG , "doOnceWork");
        String workName = "OnceName";
        // 1、构建约束条件
        Constraints constraints = new Constraints.Builder()
                //.setRequiresBatteryNotLow(true) // 设备电量充足
                //.setRequiresCharging(true) // 设备正在充电
                .setRequiredNetworkType(NetworkType.CONNECTED) // 已经连上网络
                .build();
        // 2、构建输入数据
        Data inputData = new Data.Builder()
                .putString("name", "小明")
                .putInt("height", 180)
                .putDouble("weight", 80)
                .build();
        // 3、构建一次性任务的工作请求。OneTimeWorkRequest表示一次性任务，PeriodicWorkRequest表示周期性任务
        String workTag = "OnceTag";
        OneTimeWorkRequest onceRequest = new OneTimeWorkRequest.Builder(CollectWork.class)
                .addTag(workTag) // 添加工作标签
                .setConstraints(constraints) // 设置触发条件
                .setInputData(inputData) // 设置输入参数
                .build();
        UUID workId = onceRequest.getId(); // 获取工作请求的编号
        // 4、执行工作请求
        WorkManager workManager = WorkManager.getInstance(this);
        workManager.enqueue(onceRequest); // 将工作请求加入执行队列
        // 将指定名称的工作请求加入执行队列
        //workManager.enqueueUniqueWork(workName, ExistingWorkPolicy.KEEP, onceRequest);

        // 获取指定编号的工作信息，并实时监听工作的处理结果
        workManager.getWorkInfoByIdLiveData(workId).observe(this, workInfo -> {
            Log.d(TAG, "workInfo:" + workInfo.toString());
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) { // 工作处理成功
                Data outputData = workInfo.getOutputData(); // 获得工作信息的输出数据
                int resultCode = outputData.getInt("resultCode", 0);
                String resultDesc = outputData.getString("resultDesc");
                String desc = String.format("工作处理结果为：resultCode=%d，resultDesc=%s",
                        resultCode, resultDesc);
                tv_result.setText(desc);
            }
        });
        //workManager.getWorkInfosByTagLiveData(workTag);
        //workManager.getWorkInfosForUniqueWorkLiveData(workName);

//        workManager.cancelWorkById(workId); // 取消指定编号的工作
//        workManager.cancelAllWorkByTag(workTag); // 取消指定标签的工作
//        workManager.cancelUniqueWork(workName); // 取消指定名称的工作
//        workManager.cancelAllWork(); // 取消所有工作
    }

    // 执行周期性工作
    private void doPeriodWork() {
        Log.d(TAG , "doPeriodWork");
        tv_result.setText("周期性任务请观察App日志");
        String workName = "PeriodName";
        // 1、构建约束条件
        Constraints constraints = new Constraints.Builder()
                //.setRequiresBatteryNotLow(true) // 设备电量充足
                //.setRequiresCharging(true) // 设备正在充电
                .setRequiredNetworkType(NetworkType.CONNECTED) // 已经连上网络
                .build();
        // 2、构建输入数据
        Data inputData = new Data.Builder()
                .putString("name", "小芳")
                .putInt("height", 170)
                .putDouble("weight", 60)
                .build();
        // 3、构建周期性任务的工作请求。周期性任务的间隔时间不能小于15分钟
        String workTag = "PeriodTag";
        PeriodicWorkRequest periodRequest = new PeriodicWorkRequest.Builder(
                CollectWork.class, 15, TimeUnit.MINUTES)
                .addTag(workTag) // 添加工作标签
                .setConstraints(constraints) // 设置触发条件
                .setInputData(inputData) // 设置输入参数
                .build();
        UUID workId = periodRequest.getId(); // 获取工作请求的编号
        // 4、执行工作请求
        WorkManager workManager = WorkManager.getInstance(this);
        workManager.enqueue(periodRequest); // 将工作请求加入执行队列

        // 周期性任务的观察器实际无法操纵界面，因为周期任务一直在“执行”与“等待”之间切换，不会结束处理
        workManager.getWorkInfoByIdLiveData(workId).observe(this, workInfo -> {
            Log.d(TAG, "workInfo:" + workInfo.toString());
            if (workInfo.getState() == WorkInfo.State.SUCCEEDED) { // 该分支永远都进不去
                Data outputData = workInfo.getOutputData(); // 获得工作信息的输出数据
                int resultCode = outputData.getInt("resultCode", 0);
                String resultDesc = outputData.getString("resultDesc");
                String desc = String.format("%s resultCode=%d，resultDesc=%s",
                        DateUtil.getNowTime(), resultCode, resultDesc);
                tv_result.setText(desc);
            }
        });
    }

}