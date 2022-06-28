package com.example.chapter11;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.chapter11.constant.ImageList;
import com.example.chapter11.util.StatusBarUtil;
import com.example.chapter11.util.Utils;
import com.example.chapter11.widget.BannerPager;
import com.example.chapter11.widget.PullDownRefreshLayout;

@SuppressLint("DefaultLocale")
public class PullRefreshActivity extends AppCompatActivity implements PullDownRefreshLayout.PullRefreshListener {
    private static final String TAG = "PullRefreshActivity";
    private PullDownRefreshLayout pdrl_main; // 声明一个下拉刷新布局对象
    private TextView tv_flipper; // 声明一个文本视图对象
    private LinearLayout ll_title; // 声明一个线性布局对象
    private ImageView iv_scan; // 声明一个图像视图对象
    private ImageView iv_msg; // 声明一个图像视图对象
    private boolean isDragging = false; // 是否正在拖动
    private ProgressDialog mDialog; // 声明一个进度对话框对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pull_refresh);
        pdrl_main = findViewById(R.id.pdrl_main);
        pdrl_main.setOnRefreshListener(this); // 设置下拉刷新监听器
        tv_flipper = findViewById(R.id.tv_flipper);
        ll_title = findViewById(R.id.ll_title);
        iv_scan = findViewById(R.id.iv_scan);
        iv_msg = findViewById(R.id.iv_msg);
        BannerPager banner = findViewById(R.id.banner_pager);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) banner.getLayoutParams();
        params.height = (int) (Utils.getScreenWidth(this) * 250f / 640f);
        banner.setLayoutParams(params); // 设置广告轮播条的布局参数
        banner.setImage(ImageList.getDefault()); // 设置广告轮播条的图片列表
        // 设置广告轮播条的图片点击监听器
        banner.setOnBannerListener(position -> {
            String desc = String.format("您点击了第%d张图片", position + 1);
            tv_flipper.setText(desc);
        });
        floatStatusBar(); // 添加悬浮状态栏效果
    }

    private void floatStatusBar() {
        // 让App页面扩展到状态栏区域
        StatusBarUtil.fullScreen(this);
        RelativeLayout.LayoutParams titleParams = (RelativeLayout.LayoutParams) ll_title.getLayoutParams();
        // 标题栏在上方留出一段距离，看起来仍在状态栏下方
        titleParams.topMargin = StatusBarUtil.getStatusBarHeight(this);
        ll_title.setLayoutParams(titleParams);
    }

    // 开始页面刷新
    private void beginRefresh() {
        if (mDialog == null || !mDialog.isShowing()) {
            // 显示进度对话框
            mDialog = ProgressDialog.show(this, "请稍等", "正在努力刷新页面");
            // 延迟1秒后启动刷新结束任务
            new Handler(Looper.myLooper()).postDelayed(() -> endRefresh(), 1000);
        }
    }

    // 结束页面刷新
    private void endRefresh() {
        if (isDragging) {
            mDialog.dismiss(); // 关闭进度对话框
            pdrl_main.finishRefresh();
            isDragging = false;
        }
    }

    // 计算标题栏与状态栏的渐变背景色
    private int getTitleBgColor(double scale) {
        int alpha = (int) Math.round(scale / 2 * 255);
        alpha = Math.min(alpha, 255);
        return Color.argb(alpha, 255, 255, 255);
    }

    // 在下拉刷新时触发
    @Override
    public void pullRefresh() {
        isDragging = true;
        beginRefresh(); // 开始页面刷新
    }

    // 在往上拉动时触发
    @Override
    public void pullUp(double scale) {
        int bgColor = getTitleBgColor(scale);
        ll_title.setBackgroundColor(bgColor);
        ll_title.setVisibility(View.VISIBLE);
        iv_scan.setImageResource(R.drawable.icon_scan_gray);
        iv_msg.setImageResource(R.drawable.icon_msg_gray);
        // 上拉页面，让状态栏背景渐渐变为白色
        StatusBarUtil.setStatusBarColor(this, bgColor, true);
    }

    // 在往下拉动时触发
    @Override
    public void pullDown(double scale) {
        int bgColor = getTitleBgColor(scale);
        ll_title.setBackgroundColor(bgColor);
        ll_title.setVisibility(View.VISIBLE);
        iv_scan.setImageResource(R.drawable.icon_scan_white);
        iv_msg.setImageResource(R.drawable.icon_msg_white);
        // 下拉到顶了，让状态栏背景渐渐变为透明
        StatusBarUtil.setStatusBarColor(this, bgColor, false);
    }

    @Override
    public void hideTitle() {
        ll_title.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showTitle() {
        ll_title.setVisibility(View.VISIBLE);
    }

}
