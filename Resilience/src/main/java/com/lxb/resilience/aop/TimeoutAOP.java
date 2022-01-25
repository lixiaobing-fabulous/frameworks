package com.lxb.resilience.aop;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import org.springframework.stereotype.Component;

import com.lxb.aop.annotation.Around;
import com.lxb.aop.annotation.Aspect;
import com.lxb.aop.joinpoint.MethodAopJoinPoint;
import com.lxb.resilience.annotation.Timeout;
import com.lxb.resilience.exception.TimeoutException;
import com.lxb.resilience.function.ThrowableSupplier;

@Component
@Aspect
public class TimeoutAOP {
    private final ExecutorService executor = ForkJoinPool.commonPool();

    @Around("@annotation(com.lxb.resilience.annotation.Timeout)")
    public Object doAround(MethodAopJoinPoint pjp) throws Throwable {
        Timeout         timeout   = pjp.getMethod().getAnnotation(Timeout.class);
        Future<Object>  future    = executor.submit(() -> ThrowableSupplier.execute(pjp::proceed));
        try {
            return future.get(timeout.value(), timeout.unit());
        } catch (java.util.concurrent.TimeoutException e) {
            throw new TimeoutException();
        }
    }

}
