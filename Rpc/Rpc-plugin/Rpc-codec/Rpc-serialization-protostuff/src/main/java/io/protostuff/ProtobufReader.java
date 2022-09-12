package io.protostuff;


import java.io.InputStream;

/**
 * Protobuf读入器
 */
public class ProtobufReader extends AbstractProtostuffReader {

    /**
     * 构造函数
     *
     * @param schema
     * @param inputStream
     * @param buffer
     */
    public ProtobufReader(Schema schema, InputStream inputStream, LinkedBuffer buffer) {
        super(schema, inputStream, new CodedInput(inputStream, buffer.buffer, false));
    }

}
