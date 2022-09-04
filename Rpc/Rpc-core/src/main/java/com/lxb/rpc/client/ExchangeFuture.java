package com.lxb.rpc.client;

import com.lxb.rpc.InvocationRequest;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ExchangeFuture implements Future {

    private final long createdTime;

    private InvocationRequest request;

    private Promise promise;

    private static Map<String, ExchangeFuture> workingFutureMap = new ConcurrentHashMap<>();

    public static ExchangeFuture createExchangeFuture(InvocationRequest request) {
        String requestId = request.getRequestId();
        return workingFutureMap.computeIfAbsent(requestId, id -> new ExchangeFuture(request));
    }

    public static ExchangeFuture removeExchangeFuture(String requestId) {
        return workingFutureMap.remove(requestId);
    }

    public ExchangeFuture(InvocationRequest request) {
        this.createdTime = System.currentTimeMillis();
        this.request = request;
        this.promise = new DefaultPromise(new DefaultEventLoop());
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return promise.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return promise.isCancelled();
    }

    @Override
    public boolean isDone() {
        return promise.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return promise.get();
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return promise.get(timeout, unit);
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public InvocationRequest getRequest() {
        return request;
    }

    public Promise getPromise() {
        return promise;
    }
}