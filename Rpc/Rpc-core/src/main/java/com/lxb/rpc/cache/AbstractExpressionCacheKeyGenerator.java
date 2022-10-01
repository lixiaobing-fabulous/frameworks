package com.lxb.rpc.cache;

import com.lxb.extension.Parametric;
import com.lxb.rpc.constants.Constants;
import com.lxb.rpc.exception.CacheException;
import com.lxb.rpc.expression.Expression;
import com.lxb.rpc.expression.ExpressionProvider;
import com.lxb.rpc.protocol.message.Call;
import com.lxb.rpc.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import static com.lxb.rpc.Plugin.EXPRESSION_PROVIDER;


/**
 * 抽象的表达式缓存键生成器
 */
public abstract class AbstractExpressionCacheKeyGenerator implements CacheKeyGenerator.ExpressionGenerator {

    private static final Logger logger = LoggerFactory.getLogger(AbstractExpressionCacheKeyGenerator.class);
    /**
     * 引擎插件名称
     */
    protected String             name;
    /**
     * 引擎提供者
     */
    protected ExpressionProvider provider;
    /**
     * 表达式
     */
    protected Expression expression;
    protected Parametric parametric;

    public AbstractExpressionCacheKeyGenerator(String name) {
        this.name = name;
    }

    @Override
    public void setParametric(Parametric parameters) {
        this.parametric = parameters;
    }

    @Override
    public void setup() {
        if (parametric != null) {
            String el = StringUtils.trim(parametric.getString(Constants.CACHE_KEY_EXPRESSION));
            if (el != null && !el.isEmpty()) {
                try {
                    if (provider == null) {
                        provider = name == null || name.isEmpty() ? EXPRESSION_PROVIDER.get() : EXPRESSION_PROVIDER.get(name);
                    }
                    expression = provider == null ? null : provider.build(el);
                } catch (Exception e) {
                    logger.error(String.format("Error occurs while build expression for %s", el), e);
                }
            }
        }
    }

    /**
     * 获取表达式提供者
     *
     * @return 表达式提供者
     */
    protected ExpressionProvider create() {
        return EXPRESSION_PROVIDER.get();
    }

    @Override
    public Object generate(final Call invocation) throws CacheException {
        Object[] args = invocation.getArgs();
        if (expression == null) {
            //无参
            if (args == null || args.length == 0) {
                return "";
            } else {
                return null;
            }
        }
        Method method = invocation.getMethod();
        Parameter[] parameters = method.getParameters();
        Map<String, Object> context = new HashMap<>(parameters.length + 1);
        for (int i = 0; i < parameters.length; i++) {
            context.put(parameters[i].getName(), args[i]);
        }
        context.put("args", args);
        try {
            return expression.evaluate(context);
        } catch (Exception e) {
            throw new CacheException(String.format("Error occurs while generate cache key for %s.%s", invocation.getClassName(), method.getName()));
        }
    }
}
