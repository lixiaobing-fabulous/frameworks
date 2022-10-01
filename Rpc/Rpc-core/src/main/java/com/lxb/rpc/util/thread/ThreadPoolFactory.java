package com.lxb.rpc.util.thread;


import com.lxb.extension.Extensible;
import com.lxb.extension.URL;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.lxb.rpc.constants.Constants.QUEUES_OPTION;
import static com.lxb.rpc.constants.Constants.QUEUE_TYPE_OPTION;

/**
 * 线程池
 */
@Extensible("threadpool")
@FunctionalInterface
public interface ThreadPoolFactory {

    /**
     * 构建队列
     *
     * @param size
     * @param isPriority
     * @return
     */
    BiFunction<Integer, Boolean, BlockingQueue> QUEUE_FUNCTION = (size, isPriority) -> size == 0 ? new SynchronousQueue<>() : (isPriority ?
            (size < 0 ? new PriorityBlockingQueue<>() : new PriorityBlockingQueue<>(size)) :
            (size < 0 ? new LinkedBlockingQueue<>() : new LinkedBlockingQueue<>(size)));

    /**
     * 构建线程池
     *
     * @param name          名称
     * @param url           URL
     * @param threadFactory 线程工厂类
     * @return 线程池
     */
    default ThreadPool get(final String name, final URL url, final ThreadFactory threadFactory) {
        return get(name,url, threadFactory, o -> QUEUE_FUNCTION.apply(url.getInteger(QUEUES_OPTION), !url.getString(QUEUE_TYPE_OPTION).equals(QUEUE_TYPE_OPTION.getValue())));
    }

    /**
     * 构建线程池
     *
     * @param name          名称
     * @param url           URL
     * @param threadFactory 线程工厂类
     * @param function      队列
     * @return 线程池
     */
    ThreadPool get(final String name, final URL url, final ThreadFactory threadFactory, final Function<URL, BlockingQueue> function);

}