package com.lxb.converter;

import static com.lxb.function.Streams.stream;
import static com.lxb.utils.ClassUtil.isAssignableFrom;
import static com.lxb.utils.TypeUtil.findActualTypeArgumentClass;

import java.util.ServiceLoader;
import java.util.function.Function;

import com.lxb.priority.Prioritized;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-03-22
 */
public interface Converter<S, T> extends Function<S, T>, Prioritized {
    default boolean accept(Class<?> sourceType, Class<?> targetType) {
        return isAssignableFrom(sourceType, getSourceType()) && isAssignableFrom(targetType, getTargetType());
    }


    T convert(S s);

    @Override
    default T apply(S s) {
        return convert(s);
    }

    default Class<S> getSourceType() {
        return findActualTypeArgumentClass(getClass(), Converter.class, 0);
    }

    default Class<T> getTargetType() {
        return findActualTypeArgumentClass(getClass(), Converter.class, 1);
    }

    static Converter<?, ?> getConverter(Class<?> sourceType, Class<?> targetType) {
        return stream(ServiceLoader.load(Converter.class))
                .filter(converter -> converter.accept(sourceType, targetType))
                .findFirst()
                .orElse(null);
    }

    static <T> T convertIfPossible(Object source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        Converter converter = getConverter(source.getClass(), targetType);
        if (converter != null) {
            return (T) converter.convert(source);
        }
        return null;
    }

}
