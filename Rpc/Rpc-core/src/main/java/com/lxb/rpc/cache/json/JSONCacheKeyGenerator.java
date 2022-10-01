package com.lxb.rpc.cache.json;


import com.lxb.extension.Extension;
import com.lxb.rpc.cache.CacheKeyGenerator;
import com.lxb.rpc.exception.CacheException;
import com.lxb.rpc.exception.SerializerException;
import com.lxb.rpc.protocol.message.Call;

import static com.lxb.rpc.Plugin.JSON;

/**
 * 参数转json
 */
@Extension(value = "json")
public class JSONCacheKeyGenerator implements CacheKeyGenerator {

    @Override
    public Object generate(final Call invocation) throws CacheException {
        Object[] args = invocation.getArgs();
        if (args == null || args.length == 0) {
            return "";
        }
        try {
            return JSON.get().toJSONString(args);
        } catch (SerializerException e) {
            throw new CacheException("Error occurs while generating cache key", e);
        }
    }

}
