package com.lxb.rpc.context;


import com.lxb.extension.AbstractParametric;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import static com.lxb.rpc.Plugin.CONTEXT_SUPPLIER;


/**
 * 识别变量
 */
public class Variable extends AbstractParametric {

    /**
     * 空对象
     */
    protected static final Object NULL_OBJECT = new Object();

    /**
     * 单例
     */
    public static final Variable VARIABLE = new Variable();

    protected Map<String, Object> variables = new ConcurrentHashMap<>();

    @Override
    public <T> T getObject(final String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        //从缓存里面取
        Object result = variables.computeIfAbsent(key, k -> {
            //动态上下文
            Map<String, String> dynamicGlobal = GlobalContext.getGlobalSetting();
            Object value = dynamicGlobal == null ? null : dynamicGlobal.get(k);
            //静态上下文获取
            if (value == null) {
                value = GlobalContext.get(key);
            }
            if (value == null) {
                //上下文提供者，实现了Spring和环境变量的识别
                for (ContextSupplier supplier : CONTEXT_SUPPLIER.extensions()) {
                    value = supplier.recognize(key);
                    if (value != null) {
                        break;
                    }
                }
            }
            return value == null ? NULL_OBJECT : value;
        });
        return result == NULL_OBJECT ? null : (T) result;
    }

    @Override
    public void foreach(final BiConsumer<String, Object> consumer) {
        variables.forEach(consumer);
    }

}
