package com.lxb.rpc.serialization.hessian2;


import hessian.io.Hessian2Input;

import java.io.IOException;
import java.io.InputStream;

public class Hessian2BWLInput extends Hessian2Input {


    protected InputStream inputStream;

    public Hessian2BWLInput() {
    }

    public Hessian2BWLInput(InputStream is) {
        super(is);
    }

    @Override
    public void init(final InputStream is) {
        super.init(is);
        this.inputStream = is;
    }

    @Override
    protected void validateType(final Class<?> type) throws IOException {
    }


    /**
     * 可用的字节数
     *
     * @return 可用的字节数
     * @throws IOException
     */
    public int available() throws IOException {
        return inputStream == null ? 0 : inputStream.available();
    }
}
