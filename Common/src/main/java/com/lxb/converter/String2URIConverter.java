package com.lxb.converter;

import static com.lxb.utils.StringUtils.isBlank;

import java.net.URI;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class String2URIConverter implements StringConverter<URI> {
    @Override
    public URI convert(String source) {
        if (isBlank(source)) {
            return null;
        }
        return URI.create(source);
    }
}
