package com.lxb.utils;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public abstract class ArrayUtil extends BaseUtil {
    public static <T> int length(T... values) {
        return values == null ? 0 : values.length;
    }

    public static <T> boolean isEmpty(T... values) {
        return length(values) == 0;
    }

    public static <T> boolean isNotEmpty(T... values) {
        return !isEmpty(values);
    }

}
