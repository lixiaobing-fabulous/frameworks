package hessian.io;


/**
 * 自动注册序列化
 */
public interface AutowiredObjectSerializer extends Serializer {



    /**
     * 注册的类型
     *
     * @return 类型
     */
    Class<?> getType();
}
