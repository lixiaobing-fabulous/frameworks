package com.lxb.rpc.transport;


import com.lxb.extension.URL;
import com.lxb.rpc.util.IdGenerator;

import java.net.InetSocketAddress;
import java.util.function.Supplier;

/**
 * 传输通道
 */
public interface Transport {

    /**
     * 获取URL
     *
     * @return url
     */
    URL getUrl();

    /**
     * 获取本地地址
     *
     * @return 本地地址
     */
    InetSocketAddress getLocalAddress();

    /**
     * 获取通道序号
     *
     * @return 通道序号
     */
    default int getTransportId() {
        return 0;
    }

    /**
     * 传输通道序号生成器
     */
    Supplier<Integer> ID_GENERATOR = new IdGenerator.IntIdGenerator();

}
