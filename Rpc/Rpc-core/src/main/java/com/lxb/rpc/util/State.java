package com.lxb.rpc.util;


/**
 * 状态，用于查询
 */
public interface State {

    /**
     * 是否打开中
     *
     * @return 打开中标识
     */
    boolean isOpening();

    /**
     * 是否已经打开
     *
     * @return 已经打开标识
     */
    boolean isOpened();

    /**
     * 是否正在关闭中
     *
     * @return 关闭中状态
     */
    boolean isClosing();

    /**
     * 是否已经关闭
     *
     * @return 已经关闭标识
     */
    boolean isClosed();

    /**
     * 是否关闭中或已经关闭
     *
     * @return 关闭中或已经关闭标识
     */
    boolean isClose();

    /**
     * 是否打开中或已经打开
     *
     * @return 打开中或已经打开标识
     */
    boolean isOpen();

    /**
     * 名称
     *
     * @return 名称
     */
    String name();

    /**
     * 增强状态
     */
    interface ExState extends State {

        /**
         * 是否导出中
         *
         * @return 导出中标识
         */
        boolean isExporting();

        /**
         * 是否已导出
         *
         * @return 已导出标识
         */
        boolean isExported();

        /**
         * 是否在导出中或已导出
         *
         * @return 在导出中或已导出标识
         */
        boolean isExport();

    }

}
