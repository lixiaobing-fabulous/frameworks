package com.lxb.rpc.fastjson;


import com.lxb.rpc.protocol.message.Call;
import com.lxb.rpc.protocol.message.Invocation;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * Title: Invocation fastjson 序列化<br>
 * <p/>
 * Description: <br>
 * 保证序列化字段按如下顺序：<br>
 * 1、class name 即接口名称<br>
 * 2、alias<br>
 * 3、method name<br>
 * 4、argsType callback 调用才会写
 * 5、args 参数value<br>
 * 6、attachments (值不为空则序列化)<br>
 * <p/>
 */
public class InvocationCodec extends AbstractInvocationCodec {

    public static final InvocationCodec INSTANCE = new InvocationCodec();

    @Override
    protected void fillAutowiredFor(Set<Type> types) {
        types.add(Invocation.class);
    }

    @Override
    protected Call createInvocation() {
        return new Invocation();
    }

}
