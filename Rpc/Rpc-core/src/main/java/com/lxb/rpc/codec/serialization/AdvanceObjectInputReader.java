package com.lxb.rpc.codec.serialization;


import java.io.IOException;
import java.io.ObjectInput;

/**
 * 高级的Java对象读取器
 */
public class AdvanceObjectInputReader extends ObjectInputReader {

    public AdvanceObjectInputReader(ObjectInput input) {
        super(input);
    }

    @Override
    public String readUTF() throws IOException {
        int len = input.readInt();
        if (len < 0) {
            return null;
        }
        return input.readUTF();
    }

    @Override
    public Object readObject() throws IOException {
        try {
            byte b = input.readByte();
            if (b == 0) {
                return null;
            }
            return input.readObject();
        } catch (ClassNotFoundException e) {
            return new IOException(e.getMessage(), e);
        }
    }
}