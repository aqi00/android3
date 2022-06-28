package com.example.chapter08;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.chapter08.widget.OvalView;

public class ViewInvalidateActivity extends AppCompatActivity {
    private OvalView ov_validate; // 声明一个椭圆视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_invalidate);
        ov_validate = findViewById(R.id.ov_validate);
        initRefreshSpinner(); // 初始化刷新方式的下拉框
    }

    // 初始化刷新方式的下拉框
    private void initRefreshSpinner() {
        ArrayAdapter<String> refreshAdapter = new ArrayAdapter<String>(this,
                R.layout.item_select, refreshArray);
        Spinner sp_refresh = findViewById(R.id.sp_refresh);
        sp_refresh.setPrompt("请选择刷新方式"); // 设置下拉框的标题
        sp_refresh.setAdapter(refreshAdapter); // 设置下拉框的数组适配器
        sp_refresh.setSelection(0); // 设置下拉框默认显示第一项
        // 给下拉框设置选择监听器，一旦用户选中某一项，就触发监听器的onItemSelected方法
        sp_refresh.setOnItemSelectedListener(new RefreshSelectedListener());
    }

    private String[] refreshArray = {
            "主线程调用invalidate",
            "主线程调用postInvalidate",
            "延迟3秒后刷新",
            "分线程调用invalidate",
            "分线程调用postInvalidate"
    };
    class RefreshSelectedListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (arg2 == 0) {  // 主线程调用invalidate
                ov_validate.invalidate(); // 刷新视图（用于主线程）
            } else if (arg2 == 1) {  // 主线程调用postInvalidate
                ov_validate.postInvalidate(); // 刷新视图（主线程和分线程均可使用）
            } else if (arg2 == 2) {  // 延迟3秒后刷新
                ov_validate.postInvalidateDelayed(3000); // 延迟若干时间后再刷新视图
            } else if (arg2 == 3) {  // 分线程调用invalidate
                // invalidate不是线程安全的，虽然下面代码在分线程中调用invalidate方法也没报错，但在复杂场合可能出错
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ov_validate.invalidate(); // 刷新视图（用于主线程）
                    }
                }).start();
            } else if (arg2 == 4) {  // 分线程调用postInvalidate
                // postInvalidate是线程安全的，分线程中建议调用postInvalidate方法来刷新视图
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ov_validate.postInvalidate(); // 刷新视图（主线程和分线程均可使用）
                    }
                }).start();
            }
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

}
