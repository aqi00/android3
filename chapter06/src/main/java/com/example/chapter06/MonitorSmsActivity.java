package com.example.chapter06;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("DefaultLocale")
public class MonitorSmsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MonitorSmsActivity";
    private static TextView tv_check_flow; // 声明一个文本视图对象
    private static String mCheckResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_sms);
        tv_check_flow = findViewById(R.id.tv_check_flow);
        tv_check_flow.setOnClickListener(this);
        findViewById(R.id.btn_check_flow).setOnClickListener(this);
        initSmsObserver();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_check_flow) {
            //查询数据流量，移动号码的查询方式为发送短信内容“18”给“10086”
            //电信和联通号码的短信查询方式请咨询当地运营商客服热线
            //跳到系统的短信发送页面，由用户手工发短信
            //sendSmsManual("10086", "18");
            //无需用户操作，自动发送短信
            sendSmsAuto("10086", "18");
        } else if (v.getId() == R.id.tv_check_flow) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("收到流量校准短信");
            builder.setMessage(mCheckResult);
            builder.setPositiveButton("确定", null);
            builder.create().show();
        }
    }

    // 跳到系统的短信发送页面，由用户手工编辑与发送短信
    public void sendSmsManual(String phoneNumber, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
        intent.putExtra("sms_body", message);
        startActivity(intent);
    }

    // 短信发送事件
    private String SENT_SMS_ACTION = "com.example.storage.SENT_SMS_ACTION";
    // 短信接收事件
    private String DELIVERED_SMS_ACTION = "com.example.storage.DELIVERED_SMS_ACTION";

    // 无需用户操作，由App自动发送短信
    public void sendSmsAuto(String phoneNumber, String message) {
        // 以下指定短信发送事件的详细信息
        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        sentIntent.putExtra("phone", phoneNumber);
        sentIntent.putExtra("message", message);
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 以下指定短信接收事件的详细信息
        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
        deliverIntent.putExtra("phone", phoneNumber);
        deliverIntent.putExtra("message", message);
        PendingIntent deliverPI = PendingIntent.getBroadcast(this, 1,
                deliverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 获取默认的短信管理器
        SmsManager smsManager = SmsManager.getDefault();
        // 开始发送短信内容。要确保打开发送短信的完全权限，不是那种还需提示的不完整权限
        smsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliverPI);
    }

    private Handler mHandler = new Handler(Looper.myLooper()); // 声明一个处理器对象
    private SmsGetObserver mObserver; // 声明一个短信获取的观察器对象
    private static Uri mSmsUri; // 声明一个系统短信提供器的Uri对象
    private static String[] mSmsColumn; // 声明一个短信记录的字段数组

    // 初始化短信观察器
    private void initSmsObserver() {
        //mSmsUri = Uri.parse("content://sms/inbox");
        //Android5.0之后似乎无法单独观察某个信箱，只能监控整个短信
        mSmsUri = Uri.parse("content://sms"); // 短信数据的提供器路径
        mSmsColumn = new String[]{"address", "body", "date"}; // 短信记录的字段数组
        // 创建一个短信观察器对象
        mObserver = new SmsGetObserver(this, mHandler);
        // 给指定Uri注册内容观察器，一旦发生数据变化，就触发观察器的onChange方法
        getContentResolver().registerContentObserver(mSmsUri, true, mObserver);
    }

    // 在页面销毁时触发
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mObserver); // 注销内容观察器
    }

    // 定义一个短信获取的观察器
    private static class SmsGetObserver extends ContentObserver {
        private Context mContext; // 声明一个上下文对象
        public SmsGetObserver(Context context, Handler handler) {
            super(handler);
            mContext = context;
        }

        // 观察到短信的内容提供器发生变化时触发
        public void onChange(boolean selfChange) {
            String sender = "", content = "";
            // 构建一个查询短信的条件语句，移动号码要查找10086发来的短信
            String selection = String.format("address='10086' and date>%d",
                    System.currentTimeMillis() - 1000 * 60 * 1); // 查找最近一分钟的短信
            // 通过内容解析器获取符合条件的结果集游标
            Cursor cursor = mContext.getContentResolver().query(
                    mSmsUri, mSmsColumn, selection, null, " date desc");
            // 循环取出游标所指向的所有短信记录
            while (cursor.moveToNext()) {
                sender = cursor.getString(0); // 短信的发送号码
                content = cursor.getString(1); // 短信内容
                Log.d(TAG, "sender="+sender+", content="+content);
                break;
            }
            cursor.close(); // 关闭数据库游标
            mCheckResult = String.format("发送号码：%s\n短信内容：%s", sender, content);
            // 依次解析流量校准短信里面的各项流量数值，并拼接流量校准的结果字符串
            String flow = String.format("流量校准结果如下：总流量为：%s；已使用：%s" +
                            "；剩余流量：%s", findFlow(content, "总流量为"),
                    findFlow(content, "已使用"), findFlow(content, "剩余"));
            if (tv_check_flow != null) { // 离开该页面后就不再显示流量信息
                tv_check_flow.setText(flow); // 在文本视图显示流量校准结果
            }
            super.onChange(selfChange);
        }
    }

    // 解析流量短信里面的流量数值
    private static String findFlow(String sms, String begin) {
        String flow = findString(sms, begin, "GB");
        String temp = flow.replace("GB", "").replace(".", "");
        if (!temp.matches("\\d+")) {
            flow = findString(sms, begin, "MB");
        }
        return flow;
    }

    // 截取指定头尾之间的字符串
    private static String findString(String content, String begin, String end) {
        int begin_pos = content.indexOf(begin);
        if (begin_pos < 0) {
            return "未获取";
        }
        String sub_sms = content.substring(begin_pos);
        int end_pos = sub_sms.indexOf(end);
        if (end_pos < 0) {
            return "未获取";
        }
        if (end.equals("，")) {
            return sub_sms.substring(begin.length(), end_pos);
        } else {
            return sub_sms.substring(begin.length(), end_pos + end.length());
        }
    }

}
