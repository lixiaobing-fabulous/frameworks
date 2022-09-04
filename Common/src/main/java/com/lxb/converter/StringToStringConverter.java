package com.lxb.converter;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class StringToStringConverter implements StringConverter<String> {

    @Override
    public String convert(String source) {
        return source;
    }
}
