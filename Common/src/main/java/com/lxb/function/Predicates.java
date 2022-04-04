package com.lxb.function;

import java.util.function.Predicate;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-03-22
 */
public interface Predicates {
    Predicate<?> ALWAYS_TRUE = e -> true;
    Predicate<?> ALWAYS_FALSE = e -> false;

    static <T> Predicate<T> alwaysTrue() {
        return (Predicate<T>) ALWAYS_TRUE;
    }

    static <T> Predicate<T> alwaysFalse() {
        return (Predicate<T>) ALWAYS_FALSE;
    }

    static <T> Predicate<T> and(Predicate<? super T>... predicates) {
        Predicate<T> chain = alwaysTrue();
        for (Predicate<? super T> predicate : predicates) {
            chain = chain.and(predicate);
        }
        return chain;
    }


}
