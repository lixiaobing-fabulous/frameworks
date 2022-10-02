package com.lxb.rpc;

import com.lxb.extension.ExtensionPoint;
import com.lxb.extension.ExtensionPointLazy;
import com.lxb.rpc.apm.health.Doctor;
import com.lxb.rpc.cache.CacheFactory;
import com.lxb.rpc.cluster.discovery.registry.RegistryFactory;
import com.lxb.rpc.codec.serialization.Json;
import com.lxb.rpc.codec.serialization.Serialization;
import com.lxb.rpc.context.ConfigEventHandler;
import com.lxb.rpc.context.ContextSupplier;
import com.lxb.rpc.context.Environment;
import com.lxb.rpc.event.EventBus;
import com.lxb.rpc.expression.ExpressionProvider;
import com.lxb.rpc.proxy.IDLFactory;
import com.lxb.rpc.proxy.ProxyFactory;

public interface Plugin {
    /**
     * 医生插件
     */
    ExtensionPoint<Doctor, String> DOCTOR = new ExtensionPointLazy<>(Doctor.class);

    /**
     * Proxy插件
     */
    ExtensionPoint<ProxyFactory, String> PROXY = new ExtensionPointLazy<>(ProxyFactory.class);

    /**
     * 缓存插件
     */
    ExtensionPoint<CacheFactory, String> CACHE = new ExtensionPointLazy<>(CacheFactory.class);

    /**
     * 表达式插件
     */
    ExtensionPoint<ExpressionProvider, String> EXPRESSION_PROVIDER = new ExtensionPointLazy<>(ExpressionProvider.class);

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

    /**
     * 注册中心插件
     */
    ExtensionPoint<RegistryFactory, String>    REGISTRY             = new ExtensionPointLazy<>(RegistryFactory.class);
    /**
     * 注册中心全局配置变更事件通知插件
     */
    ExtensionPoint<ConfigEventHandler, String> CONFIG_EVENT_HANDLER = new ExtensionPointLazy<>(ConfigEventHandler.class);


}
