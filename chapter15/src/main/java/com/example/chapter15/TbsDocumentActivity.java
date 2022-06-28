package com.example.chapter15;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chapter15.util.FileUtil;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsReaderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class TbsDocumentActivity extends AppCompatActivity {
    private static final String TAG = "TbsDocumentActivity";
    private RelativeLayout rl_document; // 声明一个相对布局对象
    private TbsReaderView mReaderView; // 声明一个TBS阅读器视图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tbs_document);
        rl_document = findViewById(R.id.rl_document);
        ActivityResultLauncher launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                Log.d(TAG, "uri="+uri.toString());
                String filePath = FileUtil.getPathFromContentUri(this, uri);
                Log.d(TAG, "filePath="+filePath);
                openFileInner(this, filePath); // 在阅读器视图中浏览文档
                //openFileReader(this, filePath); // 打开专门的阅读器页面浏览文档
            }
        });
        findViewById(R.id.btn_choose).setOnClickListener(v -> launcher.launch("application/*"));
        MainApplication.getInstance().qbSdkInit(); // 初始化TBS服务
    }

    // 在阅读器视图中浏览文档
    private void openFileInner(Context context, String filePath) {
        closeReader(); // 关闭阅读器
        rl_document.removeAllViews(); // 移除相对布局下的所有视图
        mReaderView = new TbsReaderView(this, (i1, o1, o2) -> {});
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mReaderView.setLayoutParams(params);
        rl_document.addView(mReaderView); // 往相对布局上添加阅读器视图

        String extension = filePath.substring(filePath.lastIndexOf(".")+1);
        Log.d(TAG, "extension="+extension);
        Bundle bundle = new Bundle();
        bundle.putString("filePath", filePath); // 指定文件路径
        // 指定文件缓存路径
        bundle.putString("tempPath", getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/tbsfile");
        // 预加载，判断格式是否正确，其中的parseFile方法是获取文件后缀
        boolean result = mReaderView.preOpen(extension, false);
        if (result) { // 文件格式正确
            mReaderView.openFile(bundle); // 在阅读器视图中打开文档
        } else { // 文件格式错误
            Toast.makeText(this, "不支持该类型的文档", Toast.LENGTH_SHORT).show();
        }
    }

    // 关闭阅读器
    private void closeReader() {
        if (mReaderView != null) {
            mReaderView.onStop(); // 阅读器视图停止工作
        }
    }

    // 打开专门的阅读器页面浏览文档
    private void openFileReader(Context context, String filePath) {
        QbSdk.canOpenFile(context, filePath, aBoolean -> Log.e(TAG,"文件能否打开："+aBoolean));
        HashMap<String, String> params = new HashMap<>();
        // 0表示文件查看器使用默认样式；1表示文件查看器使用微信样式。不设置或设置错误值，则为默认样式。
        params.put("style", "1");
        // true表示进入文件查看器；不设置或设置为false，则进入miniqb 浏览器模式。可选
        params.put("local", "true");
        // 定制文件查看器的顶部栏背景色。不设置或设置错误值，则为默认样式。
        params.put("topBarBgColor","#ffffff");
        JSONObject Object = new JSONObject();
        try {
            Object.put("pkgName", context.getPackageName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("menuData", Object.toString()); // 用来定制文件右上角弹出菜单，可传入菜单项的文本，用户点击菜单项后，sdk会通过startActivity+intent 的方式回调
        QbSdk.getMiniQBVersion(context);
        int ret = QbSdk.openFileReader(context, filePath, params, s -> {
            Log.d(TAG, "ValueCallback="+s);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeReader(); // 关闭阅读器
        QbSdk.closeFileReader(this);
    }
}