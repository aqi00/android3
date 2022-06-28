package com.example.chapter06.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class FileUtil {

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
            bitmap = BitmapFactory.decodeStream(fis); // 从文件输入流中解码位图数据
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

}
