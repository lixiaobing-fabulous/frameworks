package com.lxb.rpc.codec.serialization;


import java.io.IOException;
import java.io.ObjectOutput;

/**
 * 高级的Java对象写入器
 */
public class AdvanceObjectOutputWriter extends ObjectOutputWriter {

    public AdvanceObjectOutputWriter(final ObjectOutput output) {
        super(output);
    }

    @Override
    public void writeObject(final Object v) throws IOException {
        //保持和原有一样
        if (v == null) {
            output.writeByte(0);
        } else {
            output.writeByte(1);
            output.writeObject(v);
        }
    }

    @Override
    public void writeUTF(final String s) throws IOException {
        //保持和原有一样
        if (s == null) {
            output.writeInt(-1);
        } else {
            output.writeInt(s.length());
            output.writeUTF(s);
        }
    }
}
