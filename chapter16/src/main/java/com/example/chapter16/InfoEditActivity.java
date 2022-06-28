package com.example.chapter16;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chapter16.bean.JoinResponse;
import com.example.chapter16.constant.UrlConstant;
import com.example.chapter16.util.BitmapUtil;
import com.example.chapter16.util.DateUtil;
import com.example.chapter16.util.SharedUtil;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfoEditActivity extends AppCompatActivity {
    private final static String TAG = "InfoEditActivity";
    private int CHOOSE_CODE = 3; // 只在相册挑选图片的请求码
    private EditText et_name, et_phone, et_info; // 姓名、手机号、个人信息的编辑框
    private TextView tv_location; // 声明一个文本视图对象
    private ImageView iv_face; // 声明一个图像视图对象
    public boolean isMale = true; // 是否男性
    public int mLoveType=0; // 爱好类型
    public double mLatitude, mLongitude; // 经纬度
    public String mAddress; // 详细地址
    public Bitmap mOriginFace; // 原始的头像位图
    private ProgressDialog mDialog; // 声明一个对话框对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_edit);
        et_name = findViewById(R.id.et_name);
        RadioGroup rg_sex = findViewById(R.id.rg_sex);
        rg_sex.setOnCheckedChangeListener((group, checkedId) -> isMale = checkedId == R.id.rb_male);
        et_phone = findViewById(R.id.et_phone);
        tv_location = findViewById(R.id.tv_location);
        iv_face = findViewById(R.id.iv_face);
        et_info = findViewById(R.id.et_info);
        iv_face.setOnClickListener(v -> {
            // 创建一个内容获取动作的意图（准备跳到系统相册）
            Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
            albumIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false); // 是否允许多选
            albumIntent.setType("image/*"); // 类型为图像
            startActivityForResult(albumIntent, CHOOSE_CODE); // 打开系统相册
        });
        findViewById(R.id.btn_confirm).setOnClickListener(v -> saveInfo());
        initIntentData(); // 初始化意图数据
        initLoveSpinner(); // 初始化爱好类型下拉框
    }

    // 初始化意图数据
    private void initIntentData() {
        Bundle bundle = getIntent().getExtras();
        mLatitude = bundle.getDouble("latitude");
        mLongitude = bundle.getDouble("longitude");
        mAddress = bundle.getString("address");
        tv_location.setText(mAddress);
    }

    // 初始化爱好类型下拉框
    private void initLoveSpinner() {
        Spinner sp_love = findViewById(R.id.sp_love);
        ArrayAdapter<String> love_adapter = new ArrayAdapter<>(this,
                R.layout.item_select, loveArray);
        sp_love.setPrompt("请选择兴趣爱好");
        sp_love.setAdapter(love_adapter);
        sp_love.setOnItemSelectedListener(new LoveSelectedListener());
        sp_love.setSelection(0);
    }

    private String[] loveArray = {"唱歌", "跳舞", "绘画", "弹琴", "摄影", "出售闲置物品"};
    class LoveSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            mLoveType = arg2;
        }

        public void onNothingSelected(AdapterView<?> arg0) {}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK && requestCode == CHOOSE_CODE) { // 从相册返回
            if (intent.getData() != null) { // 从相册选择一张照片
                Uri uri = intent.getData(); // 获得已选择照片的路径对象
                // 根据指定图片的uri，获得自动缩小后的位图对象
                mOriginFace = BitmapUtil.getAutoZoomImage(this, uri, 500);
                iv_face.setImageBitmap(mOriginFace); // 设置图像视图的位图对象
            }
        }
    }

    // 保存个人信息
    private void saveInfo() {
        String name = et_name.getText().toString();
        String phone = et_phone.getText().toString();
        String info = et_info.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请先输入您的昵称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "请先输入您的手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(info)) {
            Toast.makeText(this, "请先输入要发布的信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mOriginFace == null) {
            Toast.makeText(this, "请先选择您的头像", Toast.LENGTH_SHORT).show();
            return;
        }
        int minScope = Math.min(mOriginFace.getWidth(), mOriginFace.getHeight());
        // 从原始位图裁剪出一个正方形位图
        Bitmap cropFace = Bitmap.createBitmap(mOriginFace, 0, 0, minScope, minScope);
        String path = String.format("%s/%s.jpg",
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),
                DateUtil.getNowDateTime());
        BitmapUtil.saveImage(path, cropFace); // 把位图保存为图片文件
        // 弹出进度对话框
        mDialog = ProgressDialog.show(this, "请稍候", "正在保存位置信息......");
        // 下面把用户信息（包含头像）提交给HTTP服务端
        MultipartBody.Builder builder = new MultipartBody.Builder();
        // 往建造器对象添加文本格式的分段数据
        builder.addFormDataPart("name", name); // 昵称
        builder.addFormDataPart("sex", isMale?"0":"1"); // 性别
        builder.addFormDataPart("phone", phone); // 手机号
        builder.addFormDataPart("love", loveArray[mLoveType]); // 爱好
        builder.addFormDataPart("info", info); // 发布信息
        builder.addFormDataPart("address", mAddress); // 地址
        builder.addFormDataPart("latitude", mLatitude+""); // 纬度
        builder.addFormDataPart("longitude", mLongitude+""); // 经度
        // 往建造器对象添加图像格式的分段数据
        builder.addFormDataPart("image", path.substring(path.lastIndexOf("/")),
                RequestBody.create(new File(path), MediaType.parse("image/*")));
        RequestBody body = builder.build(); // 根据建造器生成请求结构
        OkHttpClient client = new OkHttpClient(); // 创建一个okhttp客户端对象
        // 创建一个POST方式的请求结构
        Request request = new Request.Builder().post(body)
                .url(UrlConstant.HTTP_PREFIX+"joinNearby").build();
        Call call = client.newCall(request); // 根据请求结构创建调用对象
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { // 请求失败
                // 回到主线程操纵界面
                runOnUiThread(() -> {
                    mDialog.dismiss(); // 关闭进度对话框
                    Toast.makeText(InfoEditActivity.this,
                            "保存位置信息出错："+e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException { // 请求成功
                String resp = response.body().string();
                JoinResponse joinResponse = new Gson().fromJson(resp, JoinResponse.class);
                // 回到主线程操纵界面
                runOnUiThread(() -> {
                    mDialog.dismiss(); // 关闭进度对话框
                    if ("0".equals(joinResponse.getCode())) {
                        finishSave(); // 结束信息保存动作
                    } else {
                        Toast.makeText(InfoEditActivity.this, "保存位置信息失败："+joinResponse.getDesc(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // 结束信息保存动作
    private void finishSave() {
        Toast.makeText(this, "成功保存您的位置信息", Toast.LENGTH_SHORT).show();
        SharedUtil.getIntance(this).writeString("commitMyInfo", "true");
        Intent intent = new Intent(this, NearbyActivity.class);
        // 设置启动标志：跳转到新页面时，栈中的原有实例都被清空，同时开辟新任务的活动栈
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}