package com.example.chapter18.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NumberUtil {
    private final static String TAG = "NumberUtil";

    // 根据计算式描述与运算符，判断该式子的左操作数和右操作数
    public static long[] getOperands(String str, String operator) {
        String textTemplate = "(0|1|2|3|4|5|6|7|8|9|零|一|二|三|四|五|六|七|八|九|十|百|千|万|亿|两|幺)+";
        int equalPos = str.lastIndexOf("等于");
        int operatorPos = str.lastIndexOf(operator);
        String secondNum = "";
        for (int i=equalPos-1; i>=0; i--) {
            String sub = str.substring(i, equalPos);
            boolean secondResult = sub.matches(textTemplate);
            //Log.d(TAG, "sub="+sub+",secondResult="+secondResult);
            if (!secondResult) {
                break;
            }
            secondNum = sub;
        }
        if (secondNum.length() == 0) {
            return null;
        }
        String firstNum = "";
        for (int i=operatorPos-1; i>=0; i--) {
            String sub = str.substring(i, operatorPos);
            boolean firstResult = sub.matches(textTemplate);
            //Log.d(TAG, "sub="+sub+",firstResult="+firstResult);
            if (!firstResult) {
                break;
            }
            firstNum = sub;
        }
        if (firstNum.length() == 0) {
            return null;
        }
        String numberTemplate = "(0|1|2|3|4|5|6|7|8|9)+";
        Log.d(TAG, "firstNum="+firstNum+",secondNum="+secondNum);
        long firstOperand = firstNum.matches(numberTemplate) ?
                Long.parseLong(firstNum) : NumberUtil.cnNumToLong(firstNum);
        long secondOperand = secondNum.matches(numberTemplate) ?
                Long.parseLong(secondNum) : NumberUtil.cnNumToLong(secondNum);
        Log.d(TAG, "firstOperand="+firstOperand+",secondOperand="+secondOperand);
        return new long[]{firstOperand, secondOperand};
    }

    // 中文数字转为长整型数字
    public static long cnNumToLong(String chineseNumber) {
        String cnNumberStr = "零一二三四五六七八九两幺";
        String numberStr = "0123456789";
        long[] numberAmountArray = new long[]{0,1,2,3,4,5,6,7,8,9,2,1};
        String cnUintStr = "十百千万亿";
        long[] unitAmountArray = new long[]{10,100,1000,10000,10000*10000};
        List<OperateResult> resultList = new ArrayList<>();
        for (int i=0; i<chineseNumber.length(); i++) {
            char ch = chineseNumber.charAt(i);
            int numberPos = Math.max(cnNumberStr.indexOf(ch), numberStr.indexOf(ch));
            int unitPos = cnUintStr.indexOf(ch);
            long number = 1;
            long unit = 1;
            if (numberPos >= 0) {
                number = numberAmountArray[numberPos];
            } else if (unitPos >= 0) {
                unit = unitAmountArray[unitPos];
            } else {
                continue;
            }
            int size = resultList.size();
            if (size>0 && resultList.get(size-1).maxUnit==1 && unit==1) {
                for (int j=size-1; j>=0; j--) {
                    OperateResult element = resultList.get(j);
                    if (element.maxUnit <= 100) {
                        element.result *= 10;
                        element.maxUnit *= 10;
                        resultList.set(j, element);
                    }
                }
                resultList.add(new OperateResult(number, unit));
                continue;
            }
            int greaterPos = -1;
            for (int j=0; j<size; j++) {
                OperateResult element = resultList.get(j);
                //Log.d(TAG, "for element.result="+element.result+",element.maxUnit="+element.maxUnit);
                if (element.maxUnit <= unit) {
                    greaterPos = j;
                    break;
                }
            }
            Log.d(TAG, "greaterPos="+greaterPos);
            if (greaterPos >= 0) {
                long tempResult = 0;
                for (int j=size-1; j>=greaterPos; j--) {
                    OperateResult element = resultList.get(j);
                    tempResult += element.result;
                    resultList.remove(j);
                }
                resultList.add(new OperateResult(tempResult*unit, unit));
            } else {
                resultList.add(new OperateResult(number*unit, unit));
            }
        }
        long finalResult = 0;
        for (OperateResult element : resultList) {
            //Log.d(TAG, "element.result="+element.result+",element.maxUnit="+element.maxUnit);
            finalResult += element.result;
        }
        //Log.d(TAG, "finalResult="+finalResult);
        return finalResult;
    }

    // 运算结果结构
    private static class OperateResult {
        public long result; // 结果数字
        public long maxUnit; // 最大单位
        public OperateResult(long result, long maxUnit) {
            this.result = result;
            this.maxUnit = maxUnit;
        }
    }

    // 去掉小数字符串末尾的0
    public static String removeTailZero(String result) {
        if (result.contains(".")) { // 是小数
            while (true) {
                if (result.charAt(result.length() - 1) == '0')
                    result = result.substring(0, result.length() - 1);
                else {
                    if (result.endsWith(".")) {
                        result = result.substring(0, result.length() - 1);
                    }
                    break;
                }
            }
        }
        return result;
    }
}
