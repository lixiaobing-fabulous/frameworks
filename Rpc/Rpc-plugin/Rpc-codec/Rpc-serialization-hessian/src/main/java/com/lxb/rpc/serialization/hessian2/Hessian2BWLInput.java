package com.lxb.rpc.serialization.hessian2;

/*-
 * #%L
 * joyrpc
 * %%
 * Copyright (C) 2019 joyrpc.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
