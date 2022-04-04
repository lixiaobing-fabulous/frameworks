package com.lxb.serialize;

import java.nio.charset.StandardCharsets;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class StringSerializer implements Serializer<String> {
    @Override
    public byte[] serialize(String source) {
        return source.getBytes(StandardCharsets.UTF_8);
    }
}
