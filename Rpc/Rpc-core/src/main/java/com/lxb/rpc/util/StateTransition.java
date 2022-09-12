package com.lxb.rpc.util;


/**
 * 状态转换
 */
public interface StateTransition extends State {
    /**
     * 失败
     */
    int FAILED = 0;
    /**
     * 成功
     */
    int SUCCESS = 1;
    /**
     * 打开中到关闭中
     */
    int SUCCESS_OPENING_TO_CLOSING = 1;
    /**
     * 已打开到关闭中
     */
    int SUCCESS_OPENED_TO_CLOSING = 2;
    /**
     * 导出中到关闭中
     */
    int SUCCESS_EXPORTING_TO_CLOSING = 3;
    /**
     * 已导出到关闭中
     */
    int SUCCESS_EXPORTED_TO_CLOSING = 4;

    /**
     * 从关闭中到打开中
     *
     * @return 成功标识
     */
    int tryOpening();

    /**
     * 从打开中到已经打开
     *
     * @return 成功标识
     */
    int tryOpened();

    /**
     * 从已经打开中到关闭中
     *
     * @return 成功标识
     */
    int tryClosing();

    /**
     * 从关闭中到已经关闭
     *
     * @return 成功标识
     */
    int tryClosed();

    /**
     * 到已经关闭状态
     */
    void toClosed();

    /**
     * 增强状态转换
     */
    interface ExStateTransition extends StateTransition, ExState {
        /**
         * 从已关闭到导出中
         *
         * @return 成功标识
         */
        int tryExporting();

        /**
         * 从导出中到已导出
         *
         * @return 成功标识
         */
        int tryExported();
    }

}
