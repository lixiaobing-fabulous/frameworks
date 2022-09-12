package com.lxb.rpc.util;



import com.lxb.rpc.event.AsyncResult;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * Future工具类
 */
public class Futures {

    /**
     * 执行完，调用额外的逻辑
     *
     * @param future   future
     * @param runnable 执行块
     * @param <T>
     */
    public static <T> void whenComplete(final CompletableFuture<T> future, final Runnable runnable) {
        if (runnable != null) {
            future.whenComplete((v, t) -> runnable.run());
        }
    }

    /**
     * 链式调用
     *
     * @param future future
     * @param then   后续future
     * @param <T>
     */
    public static <T> void chain(final CompletableFuture<T> future, final CompletableFuture<T> then) {
        if (then != null) {
            future.whenComplete((v, t) -> {
                if (t == null) {
                    then.complete(v);
                } else {
                    then.completeExceptionally(t);
                }
            });
        }
    }

    /**
     * 链式调用，当第一个Future完成后，以异常结束
     *
     * @param future future
     * @param then   异常
     * @param <T>
     */
    public static <T> CompletableFuture<T> chain(final CompletableFuture<T> future, final Throwable then) {
        CompletableFuture<T> result = new CompletableFuture<>();
        future.whenComplete((v, t) -> result.completeExceptionally(then));
        return result;
    }

    /**
     * 出现异常
     *
     * @param future    future
     * @param throwable 异常
     */
    public static <T> void completeExceptionally(final CompletableFuture<T> future, final Throwable throwable) {
        if (future != null) {
            future.completeExceptionally(throwable);
        }
    }

    /**
     * 完成
     *
     * @param future future
     * @param value  值
     */
    public static <T> void complete(final CompletableFuture<T> future, final T value) {
        if (future != null) {
            future.complete(value);
        }
    }

    /**
     * 构造异常Future
     *
     * @param throwable 异常
     * @param <T>
     * @return future
     */
    public static <T> CompletableFuture<T> completeExceptionally(final Throwable throwable) {
        CompletableFuture<T> result = new CompletableFuture<>();
        result.completeExceptionally(throwable);
        return result;
    }

    /**
     * 保证确保所有Future执行完毕
     *
     * @param futures future列表
     * @return future
     */
    public static <T> CompletableFuture<Void> allOf(final Collection<CompletableFuture<T>> futures) {
        int size = futures.size();
        switch (size) {
            case 0:
                return CompletableFuture.completedFuture(null);
            case 1:
                CompletableFuture future = new CompletableFuture();
                Futures.chain(futures.iterator().next(), future);
                return future;
            default:
                return CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        }

    }

    /**
     * 保证确有一个Future执行完毕
     *
     * @param futures future列表
     * @return future
     */
    public static <T> CompletableFuture<Object> anyOf(final Collection<CompletableFuture<T>> futures) {
        int size = futures.size();
        switch (size) {
            case 0:
                return CompletableFuture.completedFuture(null);
            case 1:
                return futures.iterator().next().thenApply(o -> o);
            default:
                return CompletableFuture.anyOf(futures.toArray(new CompletableFuture[futures.size()]));
        }
    }

    /**
     * 确保线程在超时时间内完成
     *
     * @param future  future
     * @param timeout 超时时间
     * @param <T>
     * @return future
     */
    public static <T> CompletableFuture<T> timeout(final CompletableFuture<T> future, final long timeout) {
        //启动线程判断超时
        CountDownLatch latch = new CountDownLatch(1);
        Thread thread = new Thread(() -> {
            long time = System.currentTimeMillis();
            while (!future.isDone() && (System.currentTimeMillis() - time) < timeout) {
                try {
                    latch.await(100, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    future.completeExceptionally(e);
                }
            }
            //超时判断
            if (!future.isDone()) {
                future.completeExceptionally(new TimeoutException());
            }
        }, "offline");
        thread.start();
        future.whenComplete((v, t) -> latch.countDown());
        return future;
    }

    /**
     * 把消费者和Future组合成链
     *
     * @param consumer 消费者
     * @param future   Future
     * @param <T>
     * @return 消费者
     */
    public static <T> Consumer<AsyncResult<T>> chain(final Consumer<AsyncResult<T>> consumer, final CompletableFuture<T> future) {
        Consumer<AsyncResult<T>> c = consumer == null ? r -> {
        } : consumer;
        return future == null ? c : c.andThen(o -> {
            if (o.isSuccess()) {
                future.complete(o.getResult());
            } else {
                future.completeExceptionally(o.getThrowable());
            }
        });
    }

    /**
     * 把消费者和Future组合成链
     *
     * @param future   Future
     * @param consumer 消费者
     * @param <T>
     * @return 消费者
     */
    public static <T> CompletableFuture<T> chain(final CompletableFuture<T> future, final Consumer<AsyncResult<T>> consumer) {
        return consumer == null ? future : future.whenComplete((v, t) -> {
            if (t == null) {
                consumer.accept(new AsyncResult<>(v));
            } else {
                consumer.accept(new AsyncResult<>(v, t));
            }
        });
    }

    /**
     * 捕获异常
     *
     * @param executor 消费者
     * @param <T>
     * @return
     */
    public static <T> CompletableFuture<T> call(final Executor<T> executor) {
        CompletableFuture<T> future = new CompletableFuture<>();
        try {
            executor.execute(future);
        } catch (Exception e) {
            executor.onException(e);
            future.completeExceptionally(e);
        }
        return future;
    }

    @FunctionalInterface
    public interface Executor<T> {

        /**
         * 执行
         *
         * @param future future
         * @throws Exception
         */
        void execute(CompletableFuture<T> future) throws Exception;

        /**
         * 异常
         *
         * @param e 异常
         */
        default void onException(final Exception e) {

        }

    }

}
