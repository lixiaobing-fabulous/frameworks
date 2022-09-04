package com.lxb.rpc.util;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 系统时钟，替换 {@link System#currentTimeMillis()} ，防止CPU切换频繁
 * <p/>
 */
public class SystemClock {

    protected static final SystemClock instance = new SystemClock();
    // 精度(毫秒)
    protected long precision;
    // 当前时间
    protected volatile long now;
    // 调度任务
    protected ScheduledExecutorService scheduler;

    public static SystemClock getInstance() {
        return instance;
    }

    public SystemClock() {
        this(1L);
    }

    public SystemClock(long precision) {
        this.precision = precision;
        now = System.currentTimeMillis();
        scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "SystemClock");
            thread.setDaemon(true);
            return thread;
        });
        //TODO 时钟调整问题
        scheduler.scheduleAtFixedRate(() -> {
            now = System.currentTimeMillis();
        }, precision, precision, TimeUnit.MILLISECONDS);
    }

    public long getTime() {
        return now;
    }

    public long precision() {
        return precision;
    }

    /**
     * 获取当前时钟
     *
     * @return 当前时钟
     */
    public static long now() {
        return instance.getTime();
    }

    /**
     * 获取当前时钟，微妙
     *
     * @return
     */
    public static long microTime() {
        long microTime = now() * 1000;
        long nanoTime = System.nanoTime(); // 纳秒
        return microTime + (nanoTime % 1000000) / 1000;
    }
}
