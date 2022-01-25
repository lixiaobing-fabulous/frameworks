package com.lxb.resilience.aop;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import org.springframework.stereotype.Component;

import com.lxb.aop.annotation.Around;
import com.lxb.aop.annotation.Aspect;
import com.lxb.aop.joinpoint.MethodAopJoinPoint;
import com.lxb.resilience.annotation.RateLimit;

@Aspect
@Component
public class RateLimitAOP {
    private final ConcurrentHashMap<RateLimit, Semaphore> semaphoresCache = new ConcurrentHashMap<>();

    @Around("@annotation(com.lxb.resilience.annotation.RateLimit)")
    public Object doAround(MethodAopJoinPoint pjp) throws Throwable {
        RateLimit rateLimit = pjp.getMethod().getAnnotation(RateLimit.class);
        Semaphore semaphore = semaphoresCache.computeIfAbsent(rateLimit,
                key -> new Semaphore(rateLimit.value()));
        semaphore.acquire();
        try {
            return pjp.proceed();
        } finally {
            semaphore.release();
        }
    }

    public static class RateLimitException extends RuntimeException {
    }

}
