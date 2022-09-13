package hessian.io;


import java.io.IOException;

/**
 * 自动注册反序列化
 */
public interface AutowiredObjectDeserializer extends Deserializer {

    @Override
    default boolean isReadResolve() {
        return false;
    }

    @Override
    default Object readList(AbstractHessianInput in, int length) throws IOException {
        return null;
    }

    @Override
    default Object readLengthList(AbstractHessianInput in, int length) throws IOException {
        return null;
    }

    @Override
    default Object readMap(AbstractHessianInput in) throws IOException {
        return null;
    }

    @Override
    default Object[] createFields(int len) {
        return new Object[0];
    }

    @Override
    default Object createField(String name) {
        return null;
    }

    @Override
    default Object readObject(AbstractHessianInput in, Object[] fields) throws IOException {
        return null;
    }

    @Override
    default Object readObject(AbstractHessianInput in, String[] fieldNames) throws IOException {
        return null;
    }

    /**
     * 注册的类型
     *
     * @return 类型
     */
    Class<?> getType();
}
