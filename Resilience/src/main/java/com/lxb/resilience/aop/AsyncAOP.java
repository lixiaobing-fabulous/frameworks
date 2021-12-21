package com.lxb.resilience.aop;

import com.lxb.resilience.annotation.Asynchronous;
import com.lxb.resilience.annotation.Timeout;
import com.lxb.resilience.function.ThrowableAction;
import com.lxb.resilience.function.ThrowableSupplier;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import static java.util.concurrent.CompletableFuture.supplyAsync;

@Component
@Aspect
public class AsyncAOP {
    private final ExecutorService executor = ForkJoinPool.commonPool();

    @Around("@annotation(com.lxb.resilience.annotation.Asynchronous)")
    public Object doAround(ProceedingJoinPoint pjp) {
        MethodSignature signature  = (MethodSignature) pjp.getSignature();

        Class<?> returnType = signature.getReturnType();
        if (returnType.isAssignableFrom(Future.class)) {
            return executor.submit(() -> ThrowableAction.execute(pjp::proceed));
        } else if (returnType.isAssignableFrom(CompletionStage.class)) {
            return supplyAsync(() -> ThrowableSupplier.execute(pjp::proceed), executor);
        } else {
            throw new UnsupportedOperationException();
        }
    }

}
