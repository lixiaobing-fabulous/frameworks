package com.lxb.rpc.codec.serialization.fst;


import com.lxb.rpc.codec.serialization.ObjectOutputWriter;
import org.nustaq.serialization.FSTObjectOutput;

/**
 * FST对象写入器
 */
public class FSTObjectWriter extends ObjectOutputWriter {

    public FSTObjectWriter(FSTObjectOutput output) {
        super(output);
    }
}
