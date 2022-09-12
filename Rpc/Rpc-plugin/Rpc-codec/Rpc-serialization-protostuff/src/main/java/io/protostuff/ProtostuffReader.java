package io.protostuff;


import java.io.InputStream;

/**
 * Protostuf读入器
 */
public class ProtostuffReader extends AbstractProtostuffReader {

    /**
     * 构造函数
     *
     * @param schema
     * @param inputStream
     * @param buffer
     */
    public ProtostuffReader(Schema schema, InputStream inputStream, LinkedBuffer buffer) {
        super(schema, inputStream, new CodedInput(inputStream, buffer.buffer, true));
    }
}
