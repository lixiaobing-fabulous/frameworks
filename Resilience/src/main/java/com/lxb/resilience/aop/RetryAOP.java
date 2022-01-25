package com.lxb.resilience.aop;

import static com.lxb.resilience.utils.ClassUtils.isDerived;
import static java.util.concurrent.Executors.newScheduledThreadPool;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.springframework.stereotype.Component;

import com.lxb.aop.annotation.Around;
import com.lxb.aop.annotation.Aspect;
import com.lxb.aop.joinpoint.MethodAopJoinPoint;
import com.lxb.resilience.annotation.Retry;

import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;

@Aspect
@Component
public class RetryAOP {
    private final ScheduledExecutorService executorService = newScheduledThreadPool(8);

    @Around("@annotation(com.lxb.resilience.annotation.Retry)")
    public Object doAround(MethodAopJoinPoint pjp) throws Throwable {
        Retry retry = pjp.getMethod().getAnnotation(Retry.class);
        int maxRetries = retry.maxRetries();
        // no retry
        if (maxRetries <= 0) {
            return pjp.proceed();
        }

        Callable<ExecutionResult> execution = () -> execute(retry, pjp);
        long delay = retry.delay();

        Callable<ExecutionResult> retryExecute = () -> {
            ExecutionResult result = null;
            for (int i = 0; i < maxRetries; i++) {
                if (delay <= 0 || i == 0) {
                    // no delay
                    result = execution.call();
                } else {
                    ScheduledFuture<ExecutionResult> future =
                            executorService.schedule(execution, delay, retry.timeUnit());
                    result = future.get();
                }
                if (result.isSuccess() || shouldThrow(result.getThrowable(), retry)) {
                    break;
                }
            }
            return result;
        };
        long maxDuration = retry.maxDuration();
        ExecutionResult executionResult;

        if (maxDuration <= 0) {
            // no time limit
            executionResult = retryExecute.call();
        } else {
            Future<ExecutionResult> future = executorService.submit(retryExecute);
            executionResult = future.get(maxDuration, retry.timeUnit());
        }
        if (executionResult.isSuccess()) {
            return executionResult.getResult();
        } else {
            throw executionResult.getThrowable();
        }
    }

    boolean shouldThrow(Throwable throwable, Retry retry) {
        return !isDerived(throwable.getClass(), retry.retryFor()) || isDerived(throwable.getClass(), retry.skipFor());
    }

    @SneakyThrows
    private ExecutionResult execute(Retry retry, MethodAopJoinPoint pjp) {
        ExecutionResult.ExecutionResultBuilder builder = ExecutionResult.builder();
        try {
            builder.result(pjp.proceed())
                    .success(true);
        } catch (Throwable t) {
            Throwable cause = getCause(t);
            builder.success(false);
            builder.throwable(cause);
        }
        return builder.build();
    }

    @Data
    @Builder
    private static class ExecutionResult {
        private Object result;
        private Throwable throwable;
        private boolean success;
    }

    protected Throwable getCause(Throwable e) {
        Throwable failure = e instanceof InvocationTargetException ? e.getCause() : e;
        while (failure instanceof InvocationTargetException) {
            failure = getCause(failure);
        }
        return failure;
    }


}
