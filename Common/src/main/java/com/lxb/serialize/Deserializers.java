package com.lxb.serialize;

import static com.lxb.utils.TypeUtil.findActualTypeArgumentClasses;
import static java.util.Collections.emptyList;
import static java.util.ServiceLoader.load;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.lxb.priority.PriorityComparator;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class Deserializers {
    private final Map<Class<?>, List<Deserializer>> typedDeserializers = new HashMap<>();

    private final ClassLoader classLoader;

    public Deserializers(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Deserializers() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public void loadSPI() {
        for (Deserializer deserializer : load(Deserializer.class)) {
            List<Class<?>> typeArguments = findActualTypeArgumentClasses(deserializer.getClass(), Deserializer.class);
            Class<?> targetClass = typeArguments.isEmpty() ? Object.class : typeArguments.get(0);
            List<Deserializer> deserializers = typedDeserializers.computeIfAbsent(targetClass, k -> new LinkedList());
            deserializers.add(deserializer);
            deserializers.sort(PriorityComparator.INSTANCE);
        }
    }

    public Deserializer<?> getMostCompatible(Class<?> deserializedType) {
        Deserializer<?> deserializer = getHighestPriority(deserializedType);
        if (deserializer == null) {
            deserializer = getHighestPriority(Object.class);
        }
        return deserializer;
    }

    public <T> Deserializer<T> getHighestPriority(Class<?> deserializedType) {
        List<Deserializer<T>> serializers = get(deserializedType);
        return serializers.isEmpty() ? null : serializers.get(0);
    }

    public <T> List<Deserializer<T>> get(Class<?> deserializedType) {
        return (List) typedDeserializers.getOrDefault(deserializedType, emptyList());
    }

}
