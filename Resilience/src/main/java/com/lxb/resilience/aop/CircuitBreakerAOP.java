package com.lxb.resilience.aop;

import com.lxb.resilience.annotation.CircuitBreaker;

import com.lxb.resilience.exception.CircuitBreakerException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

import static com.lxb.resilience.utils.ClassUtils.isDerived;

@Aspect
@Component
public class CircuitBreakerAOP {
    private final ConcurrentMap<CircuitBreaker, SlidingWindow> slidingWindowsCache = new ConcurrentHashMap<>();

    @Around("@annotation(com.lxb.resilience.annotation.CircuitBreaker)")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature      = (MethodSignature) pjp.getSignature();
        CircuitBreaker  circuitBreaker = signature.getMethod().getAnnotation(CircuitBreaker.class);
        SlidingWindow   slidingWindow  = slidingWindowsCache.computeIfAbsent(circuitBreaker, key -> new SlidingWindow(key));
        slidingWindow.computeStatus();

        if (slidingWindow.isOpen()) {
            throw new CircuitBreakerException();
        }
        if (slidingWindow.shouldReset()) {
            slidingWindow.reset();
        }

        Object result = null;
        try {
            result = pjp.proceed();
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
            return currentFailureRatio() < failureRatio;
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
            return System.nanoTime() - delayTimeUnit.toNanos(delay) > openTime
                    && shouldClosed();
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
