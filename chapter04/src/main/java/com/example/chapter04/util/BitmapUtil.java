package com.example.chapter04.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;

import java.io.InputStream;

public class BitmapUtil {
    private final static String TAG = "BitmapUtil";

    // 获得旋转角度之后的位图对象
    public static Bitmap getRotateBitmap(Bitmap bitmap, float rotateDegree) {
        Matrix matrix = new Matrix(); // 创建操作图片用的矩阵对象
        matrix.postRotate(rotateDegree); // 执行图片的旋转动作
        // 创建并返回旋转后的位图对象
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, false);
    }

    // 获得比例缩放之后的位图对象
    public static Bitmap getScaleBitmap(Bitmap bitmap, double scaleRatio) {
        int new_width = (int) (bitmap.getWidth() * scaleRatio);
        int new_height = (int) (bitmap.getHeight() * scaleRatio);
        // 创建并返回缩放后的位图对象
        return Bitmap.createScaledBitmap(bitmap, new_width, new_height, false);
    }

    // 获得自动缩小后的位图对象
    public static Bitmap getAutoZoomImage(Context ctx, Uri uri) {
        Log.d(TAG, "getAutoZoomImage uri="+uri.toString());
        Bitmap zoomBitmap = null;
        // 打开指定uri获得输入流对象
        try (InputStream is = ctx.getContentResolver().openInputStream(uri)) {
            // 从输入流解码得到原始的位图对象
            Bitmap originBitmap = BitmapFactory.decodeStream(is);
            int ratio = originBitmap.getWidth()/2000+1;
            // 获得比例缩放之后的位图对象
            zoomBitmap = getScaleBitmap(originBitmap, 1.0/ratio);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zoomBitmap;
    }

}
