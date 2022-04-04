package com.lxb.utils;

import static com.lxb.utils.ArrayUtil.length;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class CollectionUtils {
    public static <T> Set<T> asSet(Collection<T> values, T... others) {
        int size = size(values);

        if (size < 1) {
            return asSet(others);
        }

        Set<T> elements = newLinkedHashSet(size + others.length);
        // add values
        elements.addAll(values);

        // add others
        for (T other : others) {
            elements.add(other);
        }
        return unmodifiableSet(elements);
    }

    public static <T> Set<T> asSet(T[] values) {
        int size = length(values);
        if (size < 1) {
            return emptySet();
        }

        Set<T> elements = newLinkedHashSet(size);
        for (int i = 0; i < size; i++) {
            elements.add(values[i]);
        }
        return unmodifiableSet(elements);
    }

    public static <T> Set<T> newLinkedHashSet(int size) {
        return newLinkedHashSet(size, Float.MIN_NORMAL);
    }

    public static <T> Set<T> newLinkedHashSet(int size, float loadFactor) {
        return new LinkedHashSet<>(size, loadFactor);
    }


    public static int size(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

}
