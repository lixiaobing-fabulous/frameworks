package com.lxb.converter;

import static com.lxb.utils.ClassLoaderUtil.getClassLoader;
import static com.lxb.utils.ClassUtil.resolveClass;
import static com.lxb.utils.StringUtils.isBlank;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class String2ClassConverter implements StringConverter<Class> {
    @Override
    public Class convert(String source) {
        if (isBlank(source)) {
            return null;
        }
        ClassLoader classLoader = getClassLoader(getClass());
        return resolveClass(source, classLoader);

    }
}
