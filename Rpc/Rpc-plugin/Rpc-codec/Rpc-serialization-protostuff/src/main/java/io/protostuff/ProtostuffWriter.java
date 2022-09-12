package io.protostuff;


import java.io.OutputStream;

/**
 * Protostuff写入器
 */
public class ProtostuffWriter extends AbstractProtostuffWriter {

    /**
     * 构造函数
     *
     * @param schema
     * @param output
     * @param outputStream
     */
    public ProtostuffWriter(Schema schema, ProtostuffOutput output, OutputStream outputStream) {
        super(schema, output, output, outputStream);
    }

}
