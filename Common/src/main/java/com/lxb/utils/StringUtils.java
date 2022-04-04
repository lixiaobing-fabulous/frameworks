package com.lxb.utils;

import static java.lang.Character.isWhitespace;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class StringUtils {
    public static int length(String value) {
        return value == null ? 0 : value.length();
    }

    public static boolean isNotEmpty(String value) {
        return length(value) > 0;
    }

    public static boolean isBlank(String value) {
        int length = length(value);
        if (length < 1) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (!isWhitespace(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
