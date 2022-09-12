package com.lxb.rpc.util;


import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * ID生成器
 */
public interface IdGenerator<M> extends Supplier<M> {

    /**
     * 整数ID生成器
     */
    class IntIdGenerator implements IdGenerator<Integer> {

        protected AtomicInteger id = new AtomicInteger(0);

        @Override
        public Integer get() {
            return id.incrementAndGet();
        }
    }

    /**
     * 流式ID生成器
     */
    class StreamIdGenerator implements IdGenerator<Integer> {

        protected AtomicInteger id;

        public StreamIdGenerator(int initialValue) {
            this.id = new AtomicInteger(initialValue);
        }

        @Override
        public Integer get() {
            return id.getAndAdd(2);
        }

    }

    /**
     * 客户端流式ID生成器
     */
    class ClientStreamIdGenerator extends StreamIdGenerator {

        public ClientStreamIdGenerator() {
            super(1);
        }

    }

    /**
     * 服务端流式ID生成器
     */
    class ServerStreamIdGenerator extends StreamIdGenerator {

        public ServerStreamIdGenerator() {
            super(2);
        }
    }

    /**
     * 长整数ID生成器
     */
    class LongIdGenerator implements IdGenerator<Long> {

        protected AtomicLong id = new AtomicLong(0);

        @Override
        public Long get() {
            return id.incrementAndGet();
        }

    }

    /**
     * 短整数转换成长整形ID生成器
     */
    class IntToLongIdGenerator implements IdGenerator<Long> {

        protected AtomicInteger id = new AtomicInteger(0);

        @Override
        public Long get() {
            return (long) id.incrementAndGet();
        }

    }

}
