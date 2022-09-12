package com.lxb.rpc;

import com.lxb.extension.ExtensionPoint;
import com.lxb.extension.ExtensionPointLazy;
import com.lxb.rpc.codec.serialization.Json;
import com.lxb.rpc.codec.serialization.Serialization;
import com.lxb.rpc.context.ContextSupplier;
import com.lxb.rpc.context.Environment;
import com.lxb.rpc.event.EventBus;
import com.lxb.rpc.proxy.IDLFactory;

public interface Plugin {

    /**
     * JSON提供者
     */
    ExtensionPoint<Json, String> JSON = new ExtensionPointLazy<>(Json.class);

    /**
     * 序列化类型提供者
     */
    ExtensionPoint<Serialization, String> SERIALIZATION = new ExtensionPointLazy<>(Serialization.class);

    /**
     * 事件总线
     */
    ExtensionPoint<EventBus, String> EVENT_BUS = new ExtensionPointLazy<>(EventBus.class);

    /**
     * 环境插件
     */
    ExtensionPoint<Environment, String> ENVIRONMENT  = new ExtensionPointLazy<>(Environment.class);
    /**
     * GRPC工厂插件
     */
    ExtensionPoint<IDLFactory, String>      GRPC_FACTORY     = new ExtensionPointLazy<>(IDLFactory.class);
    /**
     * 全局变量提供者插件
     */
    ExtensionPoint<ContextSupplier, String> CONTEXT_SUPPLIER = new ExtensionPointLazy<>(ContextSupplier.class);


}
