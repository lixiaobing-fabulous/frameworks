package com.lxb.resilience.aop;

import com.lxb.resilience.annotation.Retry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.Executors.newScheduledThreadPool;

@Aspect
@Component
public class RetryAOP {
    private final ScheduledExecutorService executorService = newScheduledThreadPool(2);

    @Around("@annotation(com.lxb.resilience.annotation.Retry)")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Retry           retry     = signature.getMethod().getAnnotation(Retry.class);
        return pjp.proceed();
    }

}
