package com.example.chapter14;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter14.bean.CommitResponse;
import com.example.chapter14.constant.UrlConstant;
import com.example.chapter14.util.BitmapUtil;
import com.example.chapter14.util.DateUtil;
import com.example.chapter14.util.MediaUtil;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShortEditActivity extends AppCompatActivity {
    private final static String TAG = "ShortEditActivity";
    private ImageView iv_cover; // 声明一个图像视图对象
    private TextView tv_date; // 声明一个文本视图对象
    private EditText et_address; // 声明一个编辑框对象
    private EditText et_label; // 声明一个编辑框对象
    private EditText et_desc; // 声明一个编辑框对象
    private Calendar calendar = Calendar.getInstance(); // 获取日历实例，里面包含了当前的年月日
    private int mCoverPos = 0; // 封面图片的序号
    private String mVideoPath; // 视频文件路径
    private Bitmap mCoverBitmap; // 声明一个位图对象
    private ProgressDialog mDialog; // 声明一个对话框对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_edit);
        mVideoPath = getIntent().getStringExtra("video_path");
        initView(); // 初始化视图
        new Thread(() -> showCover()).start(); // 启动线程展示封面图片
    }

    // 初始化视图
    private void initView() {
        iv_cover = findViewById(R.id.iv_cover);
        tv_date = findViewById(R.id.tv_date);
        et_address = findViewById(R.id.et_address);
        et_label = findViewById(R.id.et_label);
        et_desc = findViewById(R.id.et_desc);
        tv_date.setText(DateUtil.getDate(calendar));
        tv_date.setOnClickListener(v -> chooseDate());
        // 注册一个善后工作的活动结果启动器，跳到指定的活动界面
        ActivityResultLauncher launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode()==RESULT_OK && result.getData()!=null) {
                mCoverPos = result.getData().getIntExtra("cover_pos", 0);
                new Thread(() -> showCover()).start(); // 启动线程展示封面图片
            }
        });
        findViewById(R.id.btn_cover).setOnClickListener(v -> {
            // 下面跳到封面图片的更改界面
            Intent intent = new Intent(this, ShortCoverActivity.class);
            intent.putExtra("cover_pos", mCoverPos);
            intent.putExtra("video_path", mVideoPath);
            launcher.launch(intent);
        });
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_publish).setOnClickListener(v -> shortPublish());
    }

    // 选择视频拍摄日期
    private void chooseDate() {
        // 构建一个日期对话框，该对话框已经集成了日期选择器。
        // DatePickerDialog的第二个构造参数指定了日期监听器
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    tv_date.setText(DateUtil.getDate(calendar));
                },
                calendar.get(Calendar.YEAR), // 年份
                calendar.get(Calendar.MONTH), // 月份
                calendar.get(Calendar.DAY_OF_MONTH)); // 日子
        dialog.show(); // 显示日期选择对话框
    }

    // 展示封面图片
    private void showCover() {
        // 获取视频文件中的某帧图片
        mCoverBitmap = MediaUtil.getOneFrame(this, Uri.parse(mVideoPath), mCoverPos*1000);
        runOnUiThread(() -> iv_cover.setImageBitmap(mCoverBitmap)); // 回到主线程显示图片
    }

    // 执行短视频发布动作
    private void shortPublish() {
        String date = tv_date.getText().toString();
        String address = et_address.getText().toString();
        String label = et_label.getText().toString();
        String desc = et_desc.getText().toString();
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "请先输入视频拍摄地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(label)) {
            Toast.makeText(this, "请先输入视频的标签", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(desc)) {
            Toast.makeText(this, "请先输入视频的描述", Toast.LENGTH_SHORT).show();
            return;
        }
        // 弹出进度对话框
        mDialog = ProgressDialog.show(this, "请稍候", "正在发布视频信息......");
        String coverPath = String.format("%s/%s.jpg",
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),
                DateUtil.getNowDateTime());
        BitmapUtil.saveImage(coverPath, mCoverBitmap); // 把位图保存为图片文件
        // 下面把视频信息（包含封面）提交给HTTP服务端
        MultipartBody.Builder builder = new MultipartBody.Builder();
        // 往建造器对象添加文本格式的分段数据
        builder.addFormDataPart("date", date); // 拍摄日期
        builder.addFormDataPart("address", address); // 拍摄地址
        builder.addFormDataPart("label", label); // 视频标签
        builder.addFormDataPart("desc", desc); // 视频描述
        // 往建造器对象添加图像格式的分段数据
        builder.addFormDataPart("cover", coverPath.substring(coverPath.lastIndexOf("/")),
                RequestBody.create(new File(coverPath), MediaType.parse("image/*")));
        // 往建造器对象添加视频格式的分段数据
        builder.addFormDataPart("video", mVideoPath.substring(mVideoPath.lastIndexOf("/")),
                RequestBody.create(new File(mVideoPath), MediaType.parse("video/*")));
        RequestBody body = builder.build(); // 根据建造器生成请求结构

        OkHttpClient client = new OkHttpClient(); // 创建一个okhttp客户端对象
        // 创建一个POST方式的请求结构
        Request request = new Request.Builder().post(body)
                .url(UrlConstant.HTTP_PREFIX+"commitVideo").build();
        Call call = client.newCall(request); // 根据请求结构创建调用对象
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { // 请求失败
                // 回到主线程操纵界面
                runOnUiThread(() ->  {
                    mDialog.dismiss(); // 关闭进度对话框
                    Toast.makeText(ShortEditActivity.this,
                            "保存视频信息出错："+e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException { // 请求成功
                String resp = response.body().string();
                CommitResponse commitResponse = new Gson().fromJson(resp, CommitResponse.class);
                // 回到主线程操纵界面
                runOnUiThread(() -> {
                    mDialog.dismiss(); // 关闭进度对话框
                    if ("0".equals(commitResponse.getCode())) {
                        finishPublish(); // 结束视频发布动作
                    } else {
                        Toast.makeText(ShortEditActivity.this, "保存视频信息失败："+commitResponse.getDesc(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // 结束视频发布动作
    private void finishPublish() {
        Toast.makeText(this, "成功发布您的短视频", Toast.LENGTH_SHORT).show();
        // 下面重新打开短视频浏览界面
        Intent intent = new Intent(this, ShortViewActivity.class);
        // 设置启动标志：跳转到新页面时，栈中的原有实例都被清空，同时开辟新任务的活动栈
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}