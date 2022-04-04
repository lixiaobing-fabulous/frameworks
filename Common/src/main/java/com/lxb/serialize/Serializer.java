package com.lxb.serialize;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public interface Serializer<S> {
    byte[] serialize(S source);
}
