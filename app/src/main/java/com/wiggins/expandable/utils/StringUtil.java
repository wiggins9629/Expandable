package com.wiggins.expandable.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @Description String类工具集合
 * @Author 一花一世界
 */
public class StringUtil {

    /**
     * @Description 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false
     */
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim()) && !"null".equalsIgnoreCase(value.trim())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @param str       字符串只能为两位小数或者整数
     * @param isDecimal 是否是小数
     * @Description 格式化字符串，每三位用逗号隔开
     */
    public static String addComma(String str, boolean isDecimal) {
        //先将字符串颠倒顺序
        str = new StringBuilder(str).reverse().toString();
        if (str.equals("0")) {
            return str;
        }
        String str2 = "";
        for (int i = 0; i < str.length(); i++) {
            if (i * 3 + 3 > str.length()) {
                str2 += str.substring(i * 3, str.length());
                break;
            }
            str2 += str.substring(i * 3, i * 3 + 3) + ",";
        }
        if (str2.endsWith(",")) {
            str2 = str2.substring(0, str2.length() - 1);
        }
        //最后再将顺序反转过来
        String temp = new StringBuilder(str2).reverse().toString();
        if (isDecimal) {
            //去掉最后的","
            return temp.substring(0, temp.lastIndexOf(",")) + temp.substring(temp.lastIndexOf(",") + 1, temp.length());
        } else {
            return temp;
        }
    }

    /**
     * @Description 保持小数点后两位
     */
    public static String formatDoublePointTwo(double money) {
        DecimalFormat formater = new DecimalFormat();
        formater.setMaximumFractionDigits(2);
        formater.setGroupingSize(0);
        formater.setRoundingMode(RoundingMode.FLOOR);
        return formater.format(money);
    }
}
