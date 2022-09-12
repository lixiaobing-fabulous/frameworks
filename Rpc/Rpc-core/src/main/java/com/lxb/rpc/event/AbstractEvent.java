package com.lxb.rpc.event;


/**
 * 抽象的事件
 */
public abstract class AbstractEvent implements Event, Recipient {
    //事件来源
    protected Object source;
    //目标对象
    protected Object target;

    public AbstractEvent(final Object source, final Object target) {
        this.source = source;
        this.target = target;
    }

    public Object getSource() {
        return source;
    }

    @Override
    public Object getTarget() {
        return target;
    }
}
