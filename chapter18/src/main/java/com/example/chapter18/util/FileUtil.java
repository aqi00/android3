package com.example.chapter18.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
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

    // 获取指定扩展名的文件列表
    public static List<File> getFileList(String path, String[] extendArray) {
        List<File> displayedContent = new ArrayList<>();
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
        Collections.sort(displayedContent, (o1, o2) -> (o1.lastModified() > o2.lastModified()) ? -1 : 1);
        return displayedContent;
    }

    // 根据指定扩展名获取文件过滤器
    public static FilenameFilter getTypeFilter(String[] extendArray) {
        final ArrayList<String> fileExtensions = new ArrayList<>();
        Collections.addAll(fileExtensions, extendArray);
        FilenameFilter fileNameFilter = (directory, fileName) -> {
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
        };
        return fileNameFilter;
    }

    // 往文件末尾追加二进制数据
    public static void appendBytesToFile(String fileName, byte[] data) {
        try (OutputStream out = new FileOutputStream(fileName, true);
             InputStream is = new ByteArrayInputStream(data)) {
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = is.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
