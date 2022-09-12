package io.protostuff;


import java.io.OutputStream;

/**
 * Protobuf写入器
 */
public class ProtobufWriter extends AbstractProtostuffWriter {

    /**
     * 构造函数
     *
     * @param schema
     * @param output
     * @param outputStream
     */
    public ProtobufWriter(Schema schema, ProtobufOutput output, OutputStream outputStream) {
        super(schema, output, output, outputStream);
    }

}
