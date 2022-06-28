package com.example.chapter15.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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

    public static List<File> getFileList(String path, String[] extendArray) {
        List<File> displayedContent = new ArrayList<File>();
        File[] files = null;
        File directory = new File(path);
        if (extendArray != null && extendArray.length > 0) {
            FilenameFilter fileFilter = getTypeFilter(extendArray);
            files = directory.listFiles(fileFilter);
        } else {
            files = directory.listFiles();
        }

        if (files != null) {
            for (File f : files) {
                if (!f.isDirectory() && !f.isHidden()) {
                    displayedContent.add(f);
                }
            }
        }
        // 按照最后修改时间排序
        Collections.sort(displayedContent, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return (o1.lastModified() > o2.lastModified()) ? -1 : 1;
            }
        });
        return displayedContent;
    }

    public static FilenameFilter getTypeFilter(String[] extendArray) {
        final ArrayList<String> fileExtensions = new ArrayList<String>();
        for (int i = 0; i < extendArray.length; i++) {
            fileExtensions.add(extendArray[i]);
        }
        FilenameFilter fileNameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File directory, String fileName) {
                boolean matched = false;
                File f = new File(String.format("%s/%s",
                        directory.getAbsolutePath(), fileName));
                matched = f.isDirectory();
                if (!matched) {
                    for (String s : fileExtensions) {
                        s = String.format(".{0,}\\%s$", s);
                        s = s.toUpperCase(Locale.getDefault());
                        fileName = fileName.toUpperCase(Locale.getDefault());
                        matched = fileName.matches(s);
                        if (matched) {
                            break;
                        }
                    }
                }
                return matched;
            }
        };
        return fileNameFilter;
    }

    // 把字节数组保存为文件
    public static void writeFile(String path, byte[] bytes) {
        File file = new File(path);
        File dir = new File(path.substring(0, path.lastIndexOf("/")));
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (FileOutputStream os = new FileOutputStream(file)) {
            os.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 从content://media/external/file/这样的Uri中获取文件路径
    public static String getPathFromContentUri(Context context, Uri uri) {
        String path = uri.toString();
        if (path.startsWith("content://")) {
            String[] proj = new String[]{ // 媒体库的字段名称数组
                    MediaStore.Files.FileColumns._ID, // 编号
                    MediaStore.Files.FileColumns.TITLE, // 标题
                    MediaStore.Files.FileColumns.SIZE, // 文件大小
                    MediaStore.Files.FileColumns.DATA, // 文件路径
                    MediaStore.Files.FileColumns.MIME_TYPE // 媒体类型
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

    // 复制文件
    public static void copyFile(String srcPath, String destPath) {
        try (FileChannel inputChannel = new FileInputStream(srcPath).getChannel();
             FileChannel outputChannel = new FileOutputStream(destPath).getChannel();) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
