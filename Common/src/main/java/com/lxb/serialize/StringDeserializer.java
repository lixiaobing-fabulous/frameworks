package com.lxb.serialize;

import java.nio.charset.StandardCharsets;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class StringDeserializer implements Deserializer<String> {
    @Override
    public String deserialize(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
