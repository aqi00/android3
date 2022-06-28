package com.example.chapter14.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
    private final static String TAG = "FileUtil";

    // 把字符串保存到指定路径的文本文件
    public static void saveText(String path, String txt) {
        // 根据指定的文件路径构建文件输出流对象
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(txt.getBytes()); // 把字符串写入文件输出流
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 从指定路径的文本文件中读取内容字符串
    public static String openText(String path) {
        String readStr = "";
        // 根据指定的文件路径构建文件输入流对象
        try (FileInputStream fis = new FileInputStream(path)) {
            byte[] b = new byte[fis.available()];
            fis.read(b); // 从文件输入流读取字节数组
            readStr = new String(b); // 把字节数组转换为字符串
        } catch (Exception e) {
            e.printStackTrace();
        }
        return readStr; // 返回文本文件中的文本字符串
    }

    // 把位图数据保存到指定路径的图片文件
    public static void saveImage(String path, Bitmap bitmap) {
        // 根据指定的文件路径构建文件输出流对象
        try (FileOutputStream fos = new FileOutputStream(path)) {
            // 把位图数据压缩到文件输出流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 从指定路径的图片文件中读取位图数据
    public static Bitmap openImage(String path) {
        Bitmap bitmap = null; // 声明一个位图对象
        // 根据指定的文件路径构建文件输入流对象
        try (FileInputStream fis = new FileInputStream(path)) {
            // 从文件输入流中解码位图数据
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap; // 返回图片文件中的位图数据
    }

    // 检查文件是否存在，以及文件路径是否合法
    public static boolean checkFileUri(Context ctx, String path) {
        boolean result = true;
        File file = new File(path);
        if (!file.exists() || !file.isFile() || file.length() <= 0) {
            result = false;
        }
        try {
            Uri uri = Uri.parse(path); // 根据指定路径创建一个Uri对象
            // 兼容Android7.0，把访问文件的Uri方式改为FileProvider
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // 通过FileProvider获得文件的Uri访问方式
                uri = FileProvider.getUriForFile(ctx,
                        ctx.getPackageName()+".fileProvider", new File(path));
            }
        } catch (Exception e) { // 该路径可能不存在
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    // 把指定uri保存为存储卡文件
    public static void saveFileFromUri(Context ctx, Uri src, String dest) {
        try (InputStream is = ctx.getContentResolver().openInputStream(src);
             OutputStream os = new FileOutputStream(dest);) {
            int byteCount = 0;
            byte[] bytes = new byte[8096];
            while ((byteCount = is.read(bytes)) != -1){
                os.write(bytes, 0, byteCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 从content://media/external/file/这样的Uri中获取文件路径
    public static String getPathFromContentUri(Context context, Uri uri) {
        String path = uri.toString();
        if (path.startsWith("content://")) {
            String[] proj = new String[]{ // 媒体库的字段名称数组
                    MediaStore.Video.Media._ID, // 编号
                    MediaStore.Video.Media.TITLE, // 标题
                    MediaStore.Video.Media.SIZE, // 文件大小
                    MediaStore.Video.Media.MIME_TYPE, // 文件类型
                    MediaStore.Video.Media.DATA // 文件大小
            };
            try (Cursor cursor = context.getContentResolver().query(uri,
                    proj, null, null, null)) {
                cursor.moveToFirst(); // 把游标移动到开头
                if (cursor.getString(3) != null) {
                    path = cursor.getString(3);
                }
                Log.d(TAG, cursor.getLong(0) + " " + cursor.getString(1)
                        + " " + cursor.getLong(2) + " " + cursor.getString(3)
                        + " " + cursor.getString(4));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return path;
    }

}
