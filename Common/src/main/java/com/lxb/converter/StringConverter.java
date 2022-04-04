package com.lxb.converter;

import static com.lxb.utils.TypeUtil.findActualTypeArgumentClass;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
@FunctionalInterface
public interface StringConverter<T> extends Converter<String, T> {

    default Class<String> getSourceType() {
        return findActualTypeArgumentClass(getClass(), Converter.class, 1);
    }

    default Class<T> getTargetType() {
        return findActualTypeArgumentClass(getClass(), Converter.class, 0);
    }

}
