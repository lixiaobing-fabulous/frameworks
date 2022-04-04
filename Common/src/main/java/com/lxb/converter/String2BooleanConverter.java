package com.lxb.converter;

import static com.lxb.utils.StringUtils.isNotEmpty;
import static com.lxb.utils.TypeUtil.findActualTypeArgumentClass;
import static java.lang.Boolean.valueOf;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class String2BooleanConverter implements StringConverter<Boolean> {
    @Override
    public Boolean convert(String s) {
        return isNotEmpty(s) ? valueOf(s) : null;

    }

}
