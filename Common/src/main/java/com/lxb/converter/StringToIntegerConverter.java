package com.lxb.converter;

import static com.lxb.utils.StringUtils.isNotEmpty;
import static java.lang.Integer.valueOf;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class StringToIntegerConverter implements StringConverter<Integer> {

    @Override
    public Integer convert(String source) {
        return isNotEmpty(source) ? valueOf(source) : null;
    }
}
