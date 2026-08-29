package com.example.chapter13;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;

import java.util.HashMap;

public class WebBridgeActivity extends AppCompatActivity {
    private final static String TAG = "WebBridgeActivity";
    private final String mDemoPath = "file:///android_asset/jsbridge/example.html";
    private BridgeWebView bridgeWebView;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_bridge);
        bridgeWebView = findViewById(R.id.bridge_web_view);
        bridgeWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d(TAG, "JS console: " + consoleMessage.message());
                return true;
            }
        });
        bridgeWebView.loadUrl(this.mDemoPath);
        // 注册 JSBridge 入口
        bridgeWebView.registerHandler("JSBridge", (data, function) -> {
            Log.d(TAG, "JSBridge 调用，data: " + data);

            BridgeRequest request = gson.fromJson(data, BridgeRequest.class);
            if (request == null || request.methodName == null) {
                Log.e(TAG, "JSBridge 请求格式错误");
                bridgeCallback(request.callbackName, new BridgeResult(false, "请求格式错误", null));
                return;
            }

            Log.d(TAG, "JSBridge methodName: " + request.methodName + ", callbackName: " + request.callbackName);
            processBridgeRequest(request);
        });
    }

    // 获取网络信息
    private DeviceNetworkInfo getNetworkInfo() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 解析完整的网络信息
                return parseNetworkInfo(info.getType());
            }
        } catch (Exception e) {
            Log.e(TAG, "获取默认网络失败，error:", e);
        }
        return new DeviceNetworkInfo(-1, "error", "错误网络");
    }

    // 解析完整的网络信息
    private DeviceNetworkInfo parseNetworkInfo(int type) {
        if (type == ConnectivityManager.TYPE_MOBILE) {
            return new DeviceNetworkInfo(type, "mobile", "蜂窝网络");
        } else if (type == ConnectivityManager.TYPE_WIFI) {
            return new DeviceNetworkInfo(type, "wifi", "无线局域网");
        } else if (type == ConnectivityManager.TYPE_BLUETOOTH) {
            return new DeviceNetworkInfo(type, "bluetooth", "蓝牙网络");
        } else if (type == ConnectivityManager.TYPE_ETHERNET) {
            return new DeviceNetworkInfo(type, "ethernet", "以太网网络");
        } else if (type == ConnectivityManager.TYPE_VPN) {
            return new DeviceNetworkInfo(type, "vpn", "虚拟专用网络");
        } else {
            return new DeviceNetworkInfo(type, "unknown", "未知网络");
        }
    }

    // 处理 JSBridge 请求
    private void processBridgeRequest(final BridgeRequest request) {
        switch (request.methodName) {
            case "getNetworkInfo":
                // 获取当前设备的网络信息
                String seq = "";
                if (request.requestParams != null && request.requestParams.containsKey("seq")) {
                    seq = request.requestParams.get("seq");
                }
                Log.d(TAG, "getNetworkInfo seq：" + seq);
                DeviceNetworkInfo networkInfo = getNetworkInfo();
                HashMap<String, String> responseParams = new HashMap<>();
                responseParams.put("networkType", ""+networkInfo.networkType);
                responseParams.put("networkNameEN", networkInfo.networkNameEN);
                responseParams.put("networkNameCN", networkInfo.networkNameCN);
                bridgeCallback(request.callbackName, new BridgeResult(true, null, responseParams));
                break;
            default:
                Log.e(TAG, "未知 methodName: " + request.methodName);
                bridgeCallback(request.callbackName, new BridgeResult(false, "未知方法: " + request.methodName, null));
                break;
        }
    }

    // JSBridge 回调给 JS
    private void bridgeCallback(final String callbackName, final BridgeResult result) {
        final String json = gson.toJson(result);
        Log.d(TAG, "JSBridge 回调 callbackName:" + callbackName + " result:" + json);

        if (bridgeWebView == null || isFinishing() || isDestroyed()) {
            Log.w(TAG, "bridgeCallback: Activity/WebView not available, skip");
            return;
        }

        bridgeWebView.post(() -> {
            if (isFinishing() || isDestroyed() || bridgeWebView == null) {
                return;
            }
            // 直接 JS 全局回调
            String js = "window.JSBridge_onCallback(" + gson.toJson(callbackName) + ", " + json + ");";
            Log.d(TAG, "执行 JS: " + js);
            bridgeWebView.evaluateJavascript(js, null);
        });
    }

    // JSBridge 请求结构
    private static class BridgeRequest {
        public String methodName;
        public String callbackName;
        public HashMap<String, String> requestParams;
    }

    // JSBridge 响应结构
    private static class BridgeResult {
        public boolean isSuccess;
        public String errorMessage;
        public HashMap<String, String> responseParams;

        public BridgeResult(boolean isSuccess, String errorMessage, HashMap<String, String> responseParams) {
            this.isSuccess = isSuccess;
            this.errorMessage = errorMessage;
            this.responseParams = responseParams;
        }
    }

    private static class DeviceNetworkInfo {
        public int networkType;
        public String networkNameEN;
        public String networkNameCN;

        public DeviceNetworkInfo(int networkType, String networkNameEN, String networkNameCN) {
            this.networkType = networkType;
            this.networkNameEN = networkNameEN;
            this.networkNameCN = networkNameCN;
        }
    }

}