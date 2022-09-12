package com.lxb.rpc.event;


import java.io.Closeable;
import java.util.concurrent.TimeUnit;

/**
 * 消息发布者
 *
 * @date: 2019/3/12
 */
public interface Publisher<E extends Event> extends Closeable {

    /**
     * 启动
     */
    void start();

    /**
     * 关闭
     */
    void close();

    /**
     * 添加一个handler
     *
     * @param handler
     */
    boolean addHandler(EventHandler<E> handler);

    /**
     * 添加处理器
     *
     * @param handlers
     */
    default <M extends EventHandler<E>> void addHandler(final Iterable<M> handlers) {
        if (handlers != null) {
            handlers.forEach(o -> addHandler(o));
        }
    }

    /**
     * 移除一个handler
     *
     * @param handler
     */
    boolean removeHandler(EventHandler<E> handler);

    /**
     * 返回处理器的数量
     *
     * @return
     */
    int size();

    /**
     * 添加一个事件
     *
     * @param event 事件
     */
    boolean offer(E event);

    /**
     * 添加一个事件
     *
     * @param event    事件
     * @param timeout  超时时间
     * @param timeUnit 时间单位
     */
    boolean offer(E event, long timeout, TimeUnit timeUnit);
}
