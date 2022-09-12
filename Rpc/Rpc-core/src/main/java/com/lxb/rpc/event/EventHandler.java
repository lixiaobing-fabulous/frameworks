package com.lxb.rpc.event;


import java.util.function.Predicate;

/**
 * @date: 2019/3/12
 */
@FunctionalInterface
public interface EventHandler<E extends Event> {

    /**
     * 处理事件，要求事件不能阻塞
     *
     * @param event
     */
    void handle(E event);

    /**
     * 包装器
     *
     * @param predicate
     * @return
     */
    default EventHandler<E> wrap(final Predicate<E> predicate) {
        return e -> {
            if (predicate == null || predicate.test(e)) {
                handle(e);
            }
        };
    }
}
