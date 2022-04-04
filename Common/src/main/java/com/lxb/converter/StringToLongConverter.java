package com.lxb.converter;

import static com.lxb.utils.StringUtils.isNotEmpty;
import static java.lang.Long.valueOf;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class StringToLongConverter implements StringConverter<Long> {

    @Override
    public Long convert(String source) {
        return isNotEmpty(source) ? valueOf(source) : null;
    }

}
