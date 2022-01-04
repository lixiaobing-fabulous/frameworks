package com.lxb.resilience.aop;

import static com.lxb.resilience.utils.ClassUtils.isDerived;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.lxb.resilience.annotation.Fallback;

import lombok.SneakyThrows;

@Aspect
@Component
public class FallbackAOP {

    @Around("@annotation(com.lxb.resilience.annotation.Fallback)")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Fallback        fallback  = signature.getMethod().getAnnotation(Fallback.class);
        try {
            return pjp.proceed();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            if (!isDerived(cause.getClass(), fallback.fallbackFor()) || isDerived(cause.getClass(), fallback.skipFor())) {
                throw cause;
            }
            return handleFallback(pjp, fallback, t);
        }
    }

    private Object handleFallback(ProceedingJoinPoint pjp, Fallback fallback, Throwable e) throws Exception {

        String methodName     = fallback.fallbackMethod();
        Method fallbackMethod = findFallbackMethod(pjp, methodName);
        return fallbackMethod.invoke(pjp.getTarget(), pjp.getArgs());
    }

    @SneakyThrows
    private Method findFallbackMethod(ProceedingJoinPoint pjp, String methodName) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Class<?>        type      = pjp.getTarget().getClass();
        return type.getMethod(methodName, signature.getMethod().getParameterTypes());
    }

    protected Throwable getCause(Throwable e) {
        Throwable failure = e instanceof InvocationTargetException ? e.getCause() : e;
        while (failure instanceof InvocationTargetException) {
            failure = getCause(failure);
        }
        return failure;
    }

}
