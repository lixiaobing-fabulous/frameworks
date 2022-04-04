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
public class Serializers {
    private final Map<Class<?>, List<Serializer>> typedSerializers = new HashMap<>();

    private final ClassLoader classLoader;

    public Serializers(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Serializers() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public void loadSPI() {
        for (Serializer serializer : load(Serializer.class)) {
            List<Class<?>> typeArguments = findActualTypeArgumentClasses(serializer.getClass(), Serializer.class);
            Class<?> targetClass = typeArguments.isEmpty() ? Object.class : typeArguments.get(0);
            List<Serializer> serializers = typedSerializers.computeIfAbsent(targetClass, k -> new LinkedList());
            serializers.add(serializer);
            serializers.sort(PriorityComparator.INSTANCE);
        }
    }

    public Serializer<?> getMostCompatible(Class<?> serializedType) {
        Serializer<?> serializer = getHighestPriority(serializedType);
        if (serializer == null) {
            serializer = getHighestPriority(Object.class);
        }
        return serializer;
    }

    public <S> Serializer<S> getHighestPriority(Class<S> serializedType) {
        List<Serializer<S>> serializers = get(serializedType);
        return serializers.isEmpty() ? null : serializers.get(0);
    }

    public <S> List<Serializer<S>> get(Class<S> serializedType) {
        return (List) typedSerializers.getOrDefault(serializedType, emptyList());
    }


    public static void main(String[] args) {
        Serializers serializers = new Serializers();
        serializers.loadSPI();
    }

}
