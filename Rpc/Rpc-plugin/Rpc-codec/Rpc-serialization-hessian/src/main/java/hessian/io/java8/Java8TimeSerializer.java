package hessian.io.java8;


import hessian.io.AbstractHessianOutput;
import hessian.io.AbstractSerializer;

import java.io.IOException;

/**
 * Java8时间序列化器
 */
public class Java8TimeSerializer<T extends Java8TimeWrapper> extends AbstractSerializer {

    //handle 具体类型
    protected Class<T> handleType;

    protected Java8TimeSerializer(Class<T> handleType) {
        this.handleType = handleType;
    }

    public static <T extends Java8TimeWrapper> Java8TimeSerializer of(Class<T> handleType) {
        return new Java8TimeSerializer(handleType);
    }

    @Override
    public void writeObject(final Object obj, final AbstractHessianOutput out) throws IOException {
        if (obj == null) {
            out.writeNull();
            return;
        }
        T handle = null;
        try {
            handle = handleType.newInstance();
        } catch (Exception e) {
            throw new IOException(String.format("failed to instance class %s ", handleType.getName()), e);
        }
        handle.wrap(obj);
        out.writeObject(handle);
    }
}
