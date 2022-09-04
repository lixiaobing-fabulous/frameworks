package com.lxb.rpc.codec.compression;


import java.io.IOException;

/**
 * 完成
 */
public interface Finishable {

    /**
     * 完成，但是不关闭输出流
     *
     * @throws IOException
     */
    void finish() throws IOException;
}
