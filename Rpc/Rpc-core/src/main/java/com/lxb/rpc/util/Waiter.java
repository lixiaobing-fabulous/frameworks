package com.lxb.rpc.util;


import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 时间等待
 */
public interface Waiter {

    /**
     * 等待时间
     *
     * @param time     时间
     * @param timeUnit 时间单位
     * @throws InterruptedException
     */
    default void await(long time, TimeUnit timeUnit) throws InterruptedException {
        await(time, timeUnit, null);
    }

    /**
     * 等待时间
     *
     * @param time      时间
     * @param timeUnit  时间单位
     * @param condition 是否要等待
     * @throws InterruptedException
     */
    void await(long time, TimeUnit timeUnit, Supplier<Boolean> condition) throws InterruptedException;

    /**
     * 唤醒
     */
    void wakeup();

    /**
     * 采用对象的await等待
     */
    class MutexWaiter implements Waiter {

        protected Object mutex;

        /**
         * 构造函数
         */
        public MutexWaiter() {
            this(new Object());
        }

        /**
         * 构造函数
         *
         * @param mutex
         */
        public MutexWaiter(final Object mutex) {
            this.mutex = mutex == null ? new Object() : mutex;
        }

        @Override
        public void await(final long time, final TimeUnit timeUnit, final Supplier<Boolean> condition) throws InterruptedException {
            synchronized (mutex) {
                if (condition == null || condition.get()) {
                    mutex.wait(timeUnit.toMillis(time));
                }
            }
        }

        @Override
        public void wakeup() {
            synchronized (mutex) {
                mutex.notifyAll();
            }
        }

    }

    /**
     * 采用线程Sleep等待
     */
    class SleepWaiter implements Waiter {

        @Override
        public void await(final long time, final TimeUnit timeUnit, final Supplier<Boolean> condition) throws InterruptedException {
            if (condition == null || condition.get()) {
                Thread.sleep(timeUnit.toMillis(time));
            }
        }

        @Override
        public void wakeup() {

        }

    }
}
