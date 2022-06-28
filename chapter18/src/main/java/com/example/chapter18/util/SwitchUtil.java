package com.example.chapter18.util;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

public class SwitchUtil {
    private static final String TAG = "SwitchUtil";

    // 获取定位功能的开关状态
    public static boolean getLocationStatus(Context ctx) {
        // 从系统服务中获取定位管理器
        LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // 检查定位功能是否打开，若未打开则跳到系统的定位功能设置页面
    public static void checkLocationIsOpen(Context ctx, String hint) {
        if (!getLocationStatus(ctx)) {
            Toast.makeText(ctx, hint, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            ctx.startActivity(intent);
        }
    }

    // 获取无线网络的开关状态
    public static boolean getWlanStatus(Context ctx) {
        // 从系统服务中获取无线网络管理器
        WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        return wm.isWifiEnabled();
    }

    // 打开或关闭无线网络
    public static void setWlanStatus(Context ctx, boolean enabled) {
        // 从系统服务中获取无线网络管理器
        WifiManager wm = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        wm.setWifiEnabled(enabled);
    }

    // 获取数据连接的开关状态
    public static boolean getMobileDataStatus(Context ctx) {
        // 从系统服务中获取电话管理器
        TelephonyManager tm = (TelephonyManager)
                ctx.getSystemService(Context.TELEPHONY_SERVICE);
        boolean isOpen = false;
        try {
            String methodName = "getDataEnabled"; // 这是隐藏方法，需要通过反射调用
            Method method = tm.getClass().getMethod(methodName);
            isOpen = (Boolean) method.invoke(tm);
            Log.d(TAG, "getMobileDataStatus isOpen="+isOpen);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOpen;
    }

    // 是否打开数据连接
    public static boolean isMobileConnected(Context ctx) {
        boolean isOpen = false;
        if (ctx != null) {
            ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo != null) {
                Log.d(TAG, "networkInfo.isAvailable="+networkInfo.isAvailable());
                isOpen = networkInfo.isAvailable();
            }
        }
        return isOpen;
    }

}
