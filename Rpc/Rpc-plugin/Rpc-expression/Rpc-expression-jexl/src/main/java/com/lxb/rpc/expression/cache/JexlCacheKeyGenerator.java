package com.lxb.rpc.expression.cache;

import com.lxb.extension.Extension;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.cache.AbstractExpressionCacheKeyGenerator;

import static com.lxb.rpc.cache.CacheKeyGenerator.JEXL_ORDER;

/**
 * JEXL3表达式缓存键生成器
 */
@Extension(value = "jexl", order = JEXL_ORDER)
@ConditionalOnClass({"org.apache.commons.jexl3.JexlBuilder"})
public class JexlCacheKeyGenerator extends AbstractExpressionCacheKeyGenerator {

    public JexlCacheKeyGenerator() {
        super("jexl");
    }

}