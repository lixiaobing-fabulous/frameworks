package com.lxb.rpc.cache;


import com.lxb.extension.Extensible;
import com.lxb.extension.Parametric;
import com.lxb.extension.Prototype;
import com.lxb.rpc.exception.CacheException;
import com.lxb.rpc.protocol.message.Call;

/**
 * 缓存键生成器
 */
@Extensible(value = "cacheKeyGenerator")
public interface CacheKeyGenerator {

    int MVEL_ORDER = 90;

    int SPEL_ORDER = MVEL_ORDER + 10;

    int JEXL_ORDER = SPEL_ORDER + 10;

    /**
     * 产生缓存的Key
     *
     * @param invocation 调用请求
     * @return 键
     * @throws CacheException 缓存异常
     */
    Object generate(Call invocation) throws CacheException;

    /**
     * 基于表达式的键生成器
     */
    interface ExpressionGenerator extends CacheKeyGenerator, Prototype {

        /**
         * 设置参数
         *
         * @param parameters 参数
         */
        void setParametric(Parametric parameters);

        /**
         * 构建表达式
         */
        void setup();
    }
}
