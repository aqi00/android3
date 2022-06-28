package com.example.chapter14.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("DefaultLocale")
public class MediaUtil {
    private final static String TAG = "MediaUtil";

    // 格式化播放时长（mm:ss）
    public static String formatDuration(int milliseconds) {
        int seconds = milliseconds / 1000;
        int hour = seconds / 3600;
        int minute = seconds / 60;
        int second = seconds % 60;
        String str;
        if (hour > 0) {
            str = String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            str = String.format("%02d:%02d", minute, second);
        }
        return str;
    }

    // 获得音视频文件的缓存路径
    public static String getRecordFilePath(Context context, String dir_name, String extend_name) {
        String path = "";
        File recordDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + dir_name + "/");
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }
        try {
            File recordFile = File.createTempFile(DateUtil.getNowDateTime(), extend_name, recordDir);
            path = recordFile.getAbsolutePath();
            Log.d(TAG, "dir_name=" + dir_name + ", extend_name=" + extend_name + ", path=" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    // 获取视频文件中的某帧图片。pos为毫秒时间
    public static Bitmap getOneFrame(Context ctx, Uri uri, int pos) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(ctx, uri); // 将指定Uri设置为媒体数据源
//        // 获得视频的播放时长
//        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//        Log.d(TAG, "duration="+duration);
        // 获取指定时间的帧图，注意getFrameAtTime方法的时间单位是微秒
        Bitmap bitmap = retriever.getFrameAtTime(pos * 1000);
        Log.d(TAG, "getWidth="+bitmap.getWidth()+", getHeight="+bitmap.getHeight());;
        return bitmap;
    }

    // 获取视频文件中的图片帧列表。beginPos为毫秒时间，count为待获取的帧数量
    public static List<String> getFrameList(Context ctx, Uri uri, int beginPos, int count) {
        String videoPath = uri.toString();
        String videoName = videoPath.substring(videoPath.lastIndexOf("/")+1);
        if (videoName.contains(".")) {
            videoName = videoName.substring(0, videoName.lastIndexOf("."));
        }
        Log.d(TAG, "videoName="+videoName);
        List<String> pathList = new ArrayList<>();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(ctx, uri); // 将指定Uri设置为媒体数据源
        // 获得视频的播放时长
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        Log.d(TAG, "duration="+duration);
        int dura_int = Integer.parseInt(duration)/1000;
        for (int i=0; i<dura_int-beginPos/1000 && i<count; i++) { // 最多只取前多少帧
            String path = String.format("%s/%s_%d.jpg",
                    ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(), videoName, i);
            if (beginPos!=0 || !new File(path).exists()) {
                // 获取指定时间的帧图，注意getFrameAtTime方法的时间单位是微秒
                Bitmap frame = retriever.getFrameAtTime(beginPos*1000 + i*1000*1000);
                int ratio = frame.getWidth()/500+1;
                Bitmap small = BitmapUtil.getScaleBitmap(frame, 1.0/ratio);
                BitmapUtil.saveImage(path, small); // 把位图保存为图片文件
            }
            pathList.add(path);
        }
        return pathList;
    }

    // 把指定Uri的视频复制一份到内部存储空间，并返回存储路径
    public static String copyVideoFromUri(Context ctx, Uri uri) {
        Log.d(TAG, "copyVideoFromUri uri="+uri.toString());
        String videoPath = String.format("%s/%s.mp4",
                ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),
                DateUtil.getNowDateTime());
        // 打开指定uri获得输入流对象，利用缓存输入和输出流复制文件
        try (InputStream is = ctx.getContentResolver().openInputStream(uri);
             BufferedInputStream bis = new BufferedInputStream(is);
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(videoPath))) {
            // 分配长度为文件大小的字节数组。available方法返回当前位置后面的剩余部分大小
            byte[] bytes = new byte[bis.available()];
            bis.read(bytes); // 从缓存输入流中读取字节数组
            bos.write(bytes); // 把字节数组写入缓存输出流
            Log.d(TAG, "文件复制完成，源文件大小="+bytes.length+"，新文件大小="+bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoPath;
    }

}
