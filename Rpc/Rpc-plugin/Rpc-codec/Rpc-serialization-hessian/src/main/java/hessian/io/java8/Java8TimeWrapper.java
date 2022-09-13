package hessian.io.java8;


import hessian.io.HessianHandle;

import java.io.Serializable;

/**
 * Java8时间包装器
 */
public interface Java8TimeWrapper<T> extends Serializable, HessianHandle {

    /**
     * 包装对外
     *
     * @param time
     */
    void wrap(T time);

    /**
     * 实现了HessianHandle接口，hessian会调用该方法读取
     *
     * @return
     */
    T readResolve();
}
