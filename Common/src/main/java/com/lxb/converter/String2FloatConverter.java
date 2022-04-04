package com.lxb.converter;

import static com.lxb.utils.StringUtils.isNotEmpty;
import static java.lang.Float.valueOf;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class String2FloatConverter implements StringConverter<Float> {
    @Override
    public Float convert(String source) {
        return isNotEmpty(source) ? valueOf(source) : null;
    }
}
