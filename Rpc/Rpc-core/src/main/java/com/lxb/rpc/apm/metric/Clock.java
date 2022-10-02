package com.lxb.rpc.apm.metric;


import java.util.concurrent.TimeUnit;

/**
 * 时钟
 *
 * @date 2019年2月19日 下午4:36:58
 */
public interface Clock {

    Clock NANO = new NanoClock();
    Clock MILLI = new MilliClock();

    /**
     * 当前时间戳
     *
     * @return 当前时间
     */
    long getTime();

    /**
     * 单位
     *
     * @return 时间单位
     */
    TimeUnit getTimeUnit();


    /**
     * 纳秒时钟
     */
    class NanoClock implements Clock {

        @Override
        public long getTime() {
            return System.nanoTime();
        }

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.NANOSECONDS;
        }

    }

    /**
     * 毫秒时钟
     */
    class MilliClock implements Clock {


        @Override
        public long getTime() {
            return System.currentTimeMillis();
        }

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.MILLISECONDS;
        }

    }

}
