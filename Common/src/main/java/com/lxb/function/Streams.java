package com.lxb.function;


import static com.lxb.function.Predicates.and;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-03-22
 */
public interface Streams {
    static <E, L extends List<E>> List<E> filter(L values, Predicate<? super E> predicate) {
        final L result;
        if (predicate == null) {
            result = values;
        } else {
            result = (L) filterStream(values, predicate).collect(toList());
        }
        return unmodifiableList(result);
    }

    static <E, L extends List<E>> List<E> filter(L values, Predicate<? super E>... predicates) {
        return filter(values, and(predicates));
    }

    static <T, I extends Iterable<T>> Stream<T> filterStream(I values, Predicate<? super T> predicate) {
        return stream(values).filter(predicate);
    }

    static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    static <T> T filterFirst(Iterable<T> values, Predicate<? super T>... predicates) {
        return filterFirst(values, and(predicates));
    }

    static <T> T filterFirst(Iterable<T> values, Predicate<? super T> predicate) {
        return stream(values)
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }

}
