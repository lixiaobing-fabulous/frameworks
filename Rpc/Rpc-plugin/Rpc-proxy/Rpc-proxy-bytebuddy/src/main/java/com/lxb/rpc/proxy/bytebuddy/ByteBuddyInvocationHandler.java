package com.lxb.rpc.proxy.bytebuddy;



import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * The type Byte buddy invocation handler.
 *
 * @date: 2 /19/2019
 */
public class ByteBuddyInvocationHandler {


    /**
     * The Invoker.
     */
    protected InvocationHandler invoker;

    /**
     * Instantiates a new Byte buddy invocation handler.
     *
     * @param invoker the invoker
     */
    public ByteBuddyInvocationHandler(InvocationHandler invoker) {
        this.invoker = invoker;
    }

    /**
     * Invoke object.
     *
     * @param proxy  the proxy
     * @param method the method
     * @param param  the param
     * @return the object
     * @throws Throwable the throwable
     */
    @RuntimeType
    public Object invoke(@This final Object proxy, @Origin final Method method, @AllArguments final Object[] param) throws Throwable {
        return invoker.invoke(proxy, method, param);
    }


}
