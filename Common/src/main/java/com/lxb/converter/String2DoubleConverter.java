package com.lxb.converter;

import static com.lxb.utils.StringUtils.isNotEmpty;
import static java.lang.Double.valueOf;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class String2DoubleConverter implements StringConverter<Double> {
    @Override
    public Double convert(String source) {
        return isNotEmpty(source) ? valueOf(source) : null;
    }
}
