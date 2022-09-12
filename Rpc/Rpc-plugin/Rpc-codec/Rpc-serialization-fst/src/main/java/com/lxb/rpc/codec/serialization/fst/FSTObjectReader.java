package com.lxb.rpc.codec.serialization.fst;


import com.lxb.rpc.codec.serialization.ObjectInputReader;
import org.nustaq.serialization.FSTObjectInput;

/**
 * FST对象读取器
 */
public class FSTObjectReader extends ObjectInputReader {

    public FSTObjectReader(FSTObjectInput input) {
        super(input);
    }
}
