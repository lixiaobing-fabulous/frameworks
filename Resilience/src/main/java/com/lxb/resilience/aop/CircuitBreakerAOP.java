package com.lxb.resilience.aop;

import com.lxb.resilience.annotation.CircuitBreaker;
import com.lxb.resilience.annotation.Retry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CircuitBreakerAOP {

    @Around("@annotation(com.lxb.resilience.annotation.CircuitBreaker)")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature      = (MethodSignature) pjp.getSignature();
        CircuitBreaker  circuitBreaker = signature.getMethod().getAnnotation(CircuitBreaker.class);
        return pjp.proceed();
    }

}
