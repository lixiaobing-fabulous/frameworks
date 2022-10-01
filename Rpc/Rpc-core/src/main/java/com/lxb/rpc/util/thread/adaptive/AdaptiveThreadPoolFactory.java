package com.lxb.rpc.util.thread.adaptive;


import com.lxb.extension.Extension;
import com.lxb.extension.URL;
import com.lxb.rpc.constants.ExceptionCode;
import com.lxb.rpc.exception.OverloadException;
import com.lxb.rpc.util.thread.DefaultThreadPool;
import com.lxb.rpc.util.thread.ThreadPool;
import com.lxb.rpc.util.thread.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.lxb.rpc.constants.Constants.*;


/**
 * 自适应线程池
 */
@Extension(value = "adaptive")
public class AdaptiveThreadPoolFactory implements ThreadPoolFactory {

    private static final Logger logger = LoggerFactory.getLogger(AdaptiveThreadPoolFactory.class);

    @Override
    public ThreadPool get(final String name, final URL url, final ThreadFactory threadFactory, final Function<URL, BlockingQueue> function) {
        Integer maxSize = url.getPositiveInt(MAX_SIZE_OPTION);
        Integer coreSize = url.getPositive(CORE_SIZE_OPTION.getName(), maxSize);
        Integer keepAliveTime = url.getPositive(KEEP_ALIVE_TIME_OPTION.getName(), (Integer) null);
        if (maxSize == coreSize) {
            keepAliveTime = keepAliveTime == null ? 0 : keepAliveTime;
        } else if (maxSize < coreSize) {
            maxSize = coreSize;
            keepAliveTime = keepAliveTime == null ? 0 : keepAliveTime;
        } else {
            keepAliveTime = keepAliveTime == null ? KEEP_ALIVE_TIME_OPTION.getValue() : keepAliveTime;
        }
        return new DefaultThreadPool(name, coreSize, maxSize, keepAliveTime, TimeUnit.MILLISECONDS,
                function.apply(url),
                threadFactory,
                new RejectedExecutionHandler() {
                    protected int i = 1;

                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        if (i++ % 7 == 0) {
                            i = 1;
                            logger.warn(String.format("Task:%s has been reject for ThreadPool exhausted! pool:%d, active:%d, queue:%d, tasks: %d",
                                    r, executor.getPoolSize(), executor.getActiveCount(), executor.getQueue().size(), executor.getTaskCount()
                            ));
                        }
                        throw new OverloadException("Biz thread pool of provider has bean exhausted", ExceptionCode.PROVIDER_THREAD_EXHAUSTED, 0, true);
                    }
                });
    }
}
