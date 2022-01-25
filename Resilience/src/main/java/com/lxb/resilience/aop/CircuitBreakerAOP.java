package com.lxb.resilience.aop;

import static com.lxb.resilience.utils.ClassUtils.isDerived;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import org.springframework.stereotype.Component;

import com.lxb.aop.annotation.Around;
import com.lxb.aop.annotation.Aspect;
import com.lxb.aop.joinpoint.MethodAopJoinPoint;
import com.lxb.resilience.annotation.CircuitBreaker;
import com.lxb.resilience.exception.CircuitBreakerException;

@Aspect
@Component
public class CircuitBreakerAOP {
    private final ConcurrentMap<CircuitBreaker, SlidingWindow> slidingWindowsCache = new ConcurrentHashMap<>();

    @Around("@annotation(com.lxb.resilience.annotation.CircuitBreaker)")
    public Object doAround(MethodAopJoinPoint joinPoint) throws Throwable {
        CircuitBreaker  circuitBreaker = joinPoint.getMethod().getAnnotation(CircuitBreaker.class);
        SlidingWindow   slidingWindow  = slidingWindowsCache.computeIfAbsent(circuitBreaker, key -> new SlidingWindow(key));
        slidingWindow.computeStatus();
        System.out.println("状态:" + slidingWindow.status + "，请求数：" + slidingWindow.requests + ", 失败数:" + slidingWindow.failures
                + ", 成功尝试数:" + slidingWindow.successTrials);
        if (slidingWindow.isOpen()) {
            throw new CircuitBreakerException();
        }
        if (slidingWindow.shouldReset()) {
            slidingWindow.reset();
        }

        Object result = null;
        try {
            result = joinPoint.proceed();
            slidingWindow.success();
        } catch (Throwable e) {
            Throwable failure = getFailure(e);
            slidingWindow.failure(failure);
        }
        return result;
    }

    private static class SlidingWindow {
        public static final int CLOSED_STATUS    = 0;
        public static final int OPEN_STATUS      = 1;
        public static final int HALF_OPEN_STATUS = 2;


        private final Class<? extends Throwable>[] appliedFailures;
        private final Class<? extends Throwable>[] skipFailures;
        private final Long                         delay;
        private final TimeUnit                     delayTimeUnit;
        private final long                         maxVolume;
        private final long                         successThreshold;
        private final double                       failureRatio;
        private final long                         minRequest;

        private final    LongAdder requests      = new LongAdder();
        private final    LongAdder failures      = new LongAdder();
        private final    LongAdder successTrials = new LongAdder();
        private volatile int       status;
        private volatile long      createdTime;
        private volatile long      openTime;

        public SlidingWindow(CircuitBreaker circuitBreaker) {
            this.reset();
            this.appliedFailures = circuitBreaker.failOn();
            this.skipFailures = circuitBreaker.skipOn();
            this.delay = circuitBreaker.delay();
            this.delayTimeUnit = circuitBreaker.delayTimeUnit();
            this.maxVolume = circuitBreaker.maxVolume();
            this.successThreshold = circuitBreaker.successThreshold();
            this.failureRatio = circuitBreaker.failureRatio();
            this.minRequest = circuitBreaker.minVolume();
        }

        private void reset() {
            createdTime = System.nanoTime();
            close();
            requests.reset();
            failures.reset();
            successTrials.reset();
        }

        private void close() {
            this.openTime = Long.MAX_VALUE;
            this.status = CLOSED_STATUS;
        }


        private SlidingWindow success() {
            if (isHalfOpen()) {
                successTrials.increment();
            }
            requests.increment();
            return this;
        }

        private SlidingWindow failure(Throwable failure) {
            if (isFailure(failure)) {
                failures.increment();
            }
            requests.increment();
            return this;
        }

        boolean shouldReset() {
            return requests.intValue() >= maxVolume;
        }

        private boolean isFailure(Throwable failure) {
            Class<? extends Throwable> failureClass = failure.getClass();
            return isDerived(failureClass, appliedFailures) && !isDerived(failureClass, skipFailures);
        }

        boolean isHalfOpen() {
            return HALF_OPEN_STATUS == status;
        }

        boolean isOpen() {
            return OPEN_STATUS == status;
        }

        boolean isClosed() {
            return CLOSED_STATUS == status;
        }

        private double currentFailureRatio() {
            return failures.doubleValue() / requests.doubleValue();
        }

        boolean shouldClosed() {
            return Double.isNaN(currentFailureRatio()) || (requests.intValue() < minRequest) || currentFailureRatio() < failureRatio;
        }

        boolean shouldOpen() {
            return !shouldClosed();
        }

        private void open() {
            this.openTime = System.nanoTime();
            this.status = OPEN_STATUS;
        }

        private void halfOpen() {
            status = HALF_OPEN_STATUS;
        }

        boolean shouldHalfOpen() {
            return System.nanoTime() - delayTimeUnit.toNanos(delay) > openTime;
        }

        public SlidingWindow computeStatus() {
            if (isClosed()) {
                if (shouldOpen()) {
                    open();
                }
            } else if (isOpen()) {
                if (shouldHalfOpen()) {
                    halfOpen();
                }

            } else if (isHalfOpen()) {
                if (successTrials.intValue() >= successThreshold) {
                    close();
                } else if (shouldOpen()) {
                    open();
                }
            }
            return this;
        }

    }


    private Throwable getFailure(Throwable e) {
        Throwable failure = e instanceof InvocationTargetException ? e.getCause() : e;
        while (failure instanceof InvocationTargetException) {
            failure = getFailure(failure);
        }
        return failure;
    }


}
