package com.lxb.resilience.aop;

import com.lxb.resilience.annotation.RateLimit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Aspect
@Component
public class RateLimitAOP {
    private final ConcurrentHashMap<RateLimit, Semaphore> semaphoresCache = new ConcurrentHashMap<>();

    @Around("@annotation(com.lxb.resilience.annotation.RateLimit)")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        RateLimit       rateLimit = signature.getMethod().getAnnotation(RateLimit.class);
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
