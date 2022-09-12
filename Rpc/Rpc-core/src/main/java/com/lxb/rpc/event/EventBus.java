package com.lxb.rpc.event;


import com.lxb.extension.Extensible;

/**
 * 事件总线
 *
 * @date: 2019/3/12
 */
@Extensible("eventBus")
public interface EventBus {

    /**
     * 创建消息发布者
     *
     * @param group  分组，相同分组统一的派发线程
     * @param name   名称
     * @param config 配置
     * @param <E>
     * @return
     */
    <E extends Event> Publisher<E> getPublisher(String group, String name, PublisherConfig config);

    /**
     * 创建消息发布者
     *
     * @param group 分组，相同分组统一的派发线程
     * @param name  名称
     * @param <E>
     * @return
     */
    default <E extends Event> Publisher<E> getPublisher(final String group, final String name) {
        return getPublisher(group, name, null);
    }

}
