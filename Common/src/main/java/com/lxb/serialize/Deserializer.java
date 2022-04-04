package com.lxb.serialize;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public interface Deserializer<T> {
    T deserialize(byte[] bytes);
}
