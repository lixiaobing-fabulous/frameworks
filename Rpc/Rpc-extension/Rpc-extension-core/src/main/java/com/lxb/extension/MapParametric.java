/**
 *
 */
package com.lxb.extension;


import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Map参数
 */
public class MapParametric<K extends CharSequence, V> extends AbstractParametric {
    /**
     * 参数
     */
    protected final Map<K, V> parameters;

    protected MapParametric() {
        parameters = null;
    }

    public MapParametric(Map<K, V> parameters) {
        this.parameters = parameters;
    }

    @Override
    public <T> T getObject(final String key) {
        return parameters == null ? null : (T) parameters.get(key);
    }

    @Override
    public void foreach(final BiConsumer<String, Object> consumer) {
        if (consumer != null && parameters != null) {
            parameters.forEach((k, v) -> consumer.accept(k.toString(), v));
        }
    }
}
