package com.example.chapter13;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter13.constant.NetConst;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkhttpCallActivity extends AppCompatActivity {
    private final static String TAG = "OkhttpCallActivity";
    private final static String URL_STOCK = "https://hq.sinajs.cn/list=s_sh000001";
    private final static String URL_LOGIN = NetConst.HTTP_PREFIX + "login";
    private LinearLayout ll_login; // 声明一个线性布局对象
    private EditText et_username; // 声明一个编辑框对象
    private EditText et_password; // 声明一个编辑框对象
    private TextView tv_result; // 声明一个文本视图对象
    private int mCheckedId = R.id.rb_get; // 当前选中的单选按钮资源编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_okhttp_call);
        ll_login = findViewById(R.id.ll_login);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        tv_result = findViewById(R.id.tv_result);
        RadioGroup rg_method = findViewById(R.id.rg_method);
        rg_method.setOnCheckedChangeListener((group, checkedId) -> {
            mCheckedId = checkedId;
            int visibility = mCheckedId == R.id.rb_get ? View.GONE : View.VISIBLE;
            ll_login.setVisibility(visibility);
        });
        findViewById(R.id.btn_send).setOnClickListener(v -> {
            if (mCheckedId == R.id.rb_get) {
                doGet(); // 发起GET方式的HTTP请求
            } else if (mCheckedId == R.id.rb_post_form) {
                postForm(); // 发起POST方式的HTTP请求（报文为表单格式）
            } else if (mCheckedId == R.id.rb_post_json) {
                postJson(); // 发起POST方式的HTTP请求（报文为JSON格式）
            }
        });
    }

    // 发起GET方式的HTTP请求
    private void doGet() {
        OkHttpClient client = new OkHttpClient(); // 创建一个okhttp客户端对象
        // 创建一个GET方式的请求结构
        Request request = new Request.Builder()
                //.get() // 因为OkHttp默认采用get方式，所以这里可以不调get方法
                .header("Accept-Language", "zh-CN") // 给http请求添加头部信息
                .header("Referer", "https://finance.sina.com.cn") // 给http请求添加头部信息
                .url(URL_STOCK) // 指定http请求的调用地址
                .build();
        Call call = client.newCall(request); // 根据请求结构创建调用对象
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { // 请求失败
                // 回到主线程操纵界面
                runOnUiThread(() -> tv_result.setText("调用股指接口报错："+e.getMessage()));
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException { // 请求成功
                String resp = response.body().string();
                // 回到主线程操纵界面
                runOnUiThread(() -> tv_result.setText("调用股指接口返回：\n"+resp));
            }
        });
    }

    // 发起POST方式的HTTP请求（报文为表单格式）
    private void postForm() {
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();
        // 创建一个表单对象
        FormBody body = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        OkHttpClient client = new OkHttpClient(); // 创建一个okhttp客户端对象
        // 创建一个POST方式的请求结构
        Request request = new Request.Builder().post(body).url(URL_LOGIN).build();
        Call call = client.newCall(request); // 根据请求结构创建调用对象
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { // 请求失败
                // 回到主线程操纵界面
                runOnUiThread(() -> tv_result.setText("调用登录接口报错："+e.getMessage()));
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException { // 请求成功
                String resp = response.body().string();
                // 回到主线程操纵界面
                runOnUiThread(() -> tv_result.setText("调用登录接口返回：\n"+resp));
            }
        });
    }

    // 发起POST方式的HTTP请求（报文为JSON格式）
    private void postJson() {
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();
        String jsonString = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonString = jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 创建一个POST方式的请求结构
        RequestBody body = RequestBody.create(jsonString, MediaType.parse("text/plain;charset=utf-8"));
        OkHttpClient client = new OkHttpClient(); // 创建一个okhttp客户端对象
        Request request = new Request.Builder().post(body).url(URL_LOGIN).build();
        Call call = client.newCall(request); // 根据请求结构创建调用对象
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { // 请求失败
                // 回到主线程操纵界面
                runOnUiThread(() -> tv_result.setText("调用登录接口报错："+e.getMessage()));
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException { // 请求成功
                String resp = response.body().string();
                // 回到主线程操纵界面
                runOnUiThread(() -> tv_result.setText("调用登录接口返回：\n"+resp));
            }
        });
    }

}