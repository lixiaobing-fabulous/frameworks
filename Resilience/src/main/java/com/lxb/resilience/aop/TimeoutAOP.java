package com.lxb.resilience.aop;

import com.lxb.resilience.annotation.Timeout;
import com.lxb.resilience.exception.TimeoutException;
import com.lxb.resilience.function.ThrowableSupplier;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

@Component
@Aspect
public class TimeoutAOP {
    private final ExecutorService executor = ForkJoinPool.commonPool();

    @Around("@annotation(com.lxb.resilience.annotation.Timeout)")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Timeout         timeout   = signature.getMethod().getAnnotation(Timeout.class);
        Future<Object>  future    = executor.submit(() -> ThrowableSupplier.execute(pjp::proceed));
        try {
            return future.get(timeout.value(), timeout.unit());
        } catch (java.util.concurrent.TimeoutException e) {
            throw new TimeoutException();
        }
    }

}
