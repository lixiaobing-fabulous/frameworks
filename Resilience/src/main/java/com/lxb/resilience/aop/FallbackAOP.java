package com.lxb.resilience.aop;

import static com.lxb.resilience.utils.ClassUtils.isDerived;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.stereotype.Component;

import com.lxb.aop.annotation.Around;
import com.lxb.aop.annotation.Aspect;
import com.lxb.aop.joinpoint.MethodAopJoinPoint;
import com.lxb.resilience.annotation.Fallback;

import lombok.SneakyThrows;

@Aspect
@Component
public class FallbackAOP {

    @Around("@annotation(com.lxb.resilience.annotation.Fallback)")
    public Object doAround(MethodAopJoinPoint pjp) throws Throwable {
        Fallback fallback = pjp.getMethod().getAnnotation(Fallback.class);
        try {
            return pjp.proceed();
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            if (!isDerived(cause.getClass(), fallback.fallbackFor()) || isDerived(cause.getClass(),
                    fallback.skipFor())) {
                throw cause;
            }
            return handleFallback(pjp, fallback, t);
        }
    }

    private Object handleFallback(MethodAopJoinPoint pjp, Fallback fallback, Throwable e) throws Exception {

        String methodName = fallback.fallbackMethod();
        Method fallbackMethod = findFallbackMethod(pjp, methodName);
        return fallbackMethod.invoke(pjp.getThis(), pjp.getParameters());
    }

    @SneakyThrows
    private Method findFallbackMethod(MethodAopJoinPoint pjp, String methodName) {
        Class<?> type = pjp.getThis().getClass();
        return type.getMethod(methodName, pjp.getMethod().getParameterTypes());
    }

    protected Throwable getCause(Throwable e) {
        Throwable failure = e instanceof InvocationTargetException ? e.getCause() : e;
        while (failure instanceof InvocationTargetException) {
            failure = getCause(failure);
        }
        return failure;
    }

}
