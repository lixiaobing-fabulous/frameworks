package com.lxb.rpc.util;



import java.util.concurrent.CompletableFuture;

/**
 * 状态控制器，用于处理打开和关闭的业务逻辑
 */
public interface StateController<T> {

    /**
     * 打开
     *
     * @return CompletableFuture
     */
    CompletableFuture<T> open();

    /**
     * 优雅关闭
     *
     * @param gracefully 优雅关闭标识
     * @return CompletableFuture
     */
    CompletableFuture<T> close(boolean gracefully);

    /**
     * 关闭前进行中断
     */
    default void fireClose() {

    }

    /**
     * 增强状态控制器
     *
     * @param <T>
     */
    interface ExStateController<T> extends StateController<T> {

        /**
         * 导出
         *
         * @return CompletableFuture
         */
        CompletableFuture<T> export();
    }

}
