package com.example.chapter18.util;

import net.sourceforge.pinyin4j.PinyinHelper;

public class PinyinUtil {

    // 把汉字串转为拼音串
    public static String getHanziPinYin(String hanzi, boolean isRetainTone) {
        String result = null;
        if(null != hanzi && !"".equals(hanzi)) {
            char[] charArray = hanzi.toCharArray();
            StringBuffer sb = new StringBuffer();
            for (char ch : charArray) {
                // 逐个汉字转成拼音
                String[] stringArray = PinyinHelper.toHanyuPinyinStringArray(ch);
                if(null != stringArray) {
                    if (isRetainTone) { // 保留声调数字
                        sb.append(stringArray[0]);
                    } else { // 不保留声调数字，则去掉声调数字
                        sb.append(stringArray[0].replaceAll("\\d", ""));
                    }
                }
            }
            if(sb.length() > 0) {
                result = sb.toString();
            }
        }
        return result;
    }

}
