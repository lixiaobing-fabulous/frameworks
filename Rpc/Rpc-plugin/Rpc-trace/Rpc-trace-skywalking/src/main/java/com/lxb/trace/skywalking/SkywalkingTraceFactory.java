package com.lxb.trace.skywalking;


import com.lxb.extension.Extension;
import com.lxb.extension.Parametric;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.extension.condition.ConditionalOnProperty;
import com.lxb.rpc.apm.trace.TraceFactory;
import com.lxb.rpc.apm.trace.Tracer;
import com.lxb.rpc.context.Variable;
import com.lxb.rpc.protocol.message.Invocation;
import com.lxb.rpc.protocol.message.RequestMessage;
import org.apache.skywalking.apm.agent.core.context.CarrierItem;
import org.apache.skywalking.apm.agent.core.context.ContextCarrier;
import org.apache.skywalking.apm.agent.core.context.ContextManager;
import org.apache.skywalking.apm.agent.core.context.ContextSnapshot;
import org.apache.skywalking.apm.agent.core.context.tag.StringTag;
import org.apache.skywalking.apm.agent.core.context.trace.AbstractSpan;
import org.apache.skywalking.apm.agent.core.context.trace.SpanLayer;
import org.apache.skywalking.apm.network.trace.component.OfficialComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * skywalking跟踪工厂
 */
@Extension(value = "skywalking", order = TraceFactory.ORDER_SKYWALKING)
@ConditionalOnProperty(value = "extension.skywalking.enable", matchIfMissing = true)
@ConditionalOnClass("org.apache.skywalking.apm.agent.core.context.ContextManager")
public class SkywalkingTraceFactory implements TraceFactory {

    /**
     * 隐藏属性的key：分布式跟踪 数据KEY
     */
    public static final String HIDDEN_KEY_TRACE_SKYWALKING = ".skywalking";
    public static final int COMPONENT_ID = 2999;
    protected int componentId;

    public SkywalkingTraceFactory() {
        Parametric parametric = Variable.VARIABLE;
        componentId = parametric.getInteger("skywalking.component.id", COMPONENT_ID);
    }

    @Override
    public Tracer create(final RequestMessage<Invocation> request) {
        return request.isConsumer() ? new ConsumerTracer(request, componentId) : new ProviderTracer(request, componentId);
    }

    /**
     * 抽象的跟踪
     */
    protected static abstract class AbstractTracer implements Tracer {
        protected RequestMessage<Invocation> request;
        protected Invocation invocation;
        protected ContextSnapshot snapshot;
        protected AbstractSpan span;
        protected int componentId;

        public AbstractTracer(RequestMessage<Invocation> request, int componentId) {
            this.request = request;
            this.componentId = componentId;
            this.invocation = request.getPayLoad();
        }

        @Override
        public void snapshot() {
        }

        @Override
        public void prepare() {
            span.prepareForAsync();
            ContextManager.stopSpan(span);
        }

        @Override
        public void restore() {
        }

        /**
         * 打标签
         *
         * @param tags 标签
         */
        protected void tag(final Map<String, String> tags) {
            if (tags != null) {
                tags.forEach((key, value) -> {
                    StringTag tag = new StringTag(key);
                    tag.set(span, value);
                });
            }
        }

        @Override
        public void end(final Throwable throwable) {
            if (throwable != null) {
                span.errorOccurred();
                span.log(throwable);
            }
            span.asyncFinish();
        }
    }

    /**
     * 消费者跟踪
     */
    protected static class ConsumerTracer extends AbstractTracer {

        public ConsumerTracer(RequestMessage<Invocation> request, int componentId) {
            super(request, componentId);
        }

        @Override
        public void begin(final String name, final String component, final Map<String, String> tags) {
            Map<String, String> ctx = new HashMap<>();
            ContextCarrier contextCarrier = new ContextCarrier();
            span = ContextManager.createExitSpan(name, contextCarrier, "unknown");
            CarrierItem next = contextCarrier.items();
            while (next.hasNext()) {
                next = next.next();
                ctx.put(next.getHeadKey(), next.getHeadValue());
            }
            invocation.addAttachment(HIDDEN_KEY_TRACE_SKYWALKING, ctx);
            span.setComponent(new OfficialComponent(componentId, component));
            tag(tags);
            SpanLayer.asRPCFramework(span);
        }
    }

    /**
     * 生产者跟踪
     */
    protected static class ProviderTracer extends AbstractTracer {

        public ProviderTracer(RequestMessage<Invocation> request, int componentId) {
            super(request, componentId);
        }

        @Override
        public void begin(final String name, final String component, final Map<String, String> tags) {
            Map<String, String> ctx = (Map<String, String>) invocation.removeAttachment(HIDDEN_KEY_TRACE_SKYWALKING);
            ContextCarrier contextCarrier = new ContextCarrier();
            if (ctx != null) {
                CarrierItem next = contextCarrier.items();
                while (next.hasNext()) {
                    next = next.next();
                    next.setHeadValue(ctx.get(next.getHeadKey()));
                }
            }
            span = ContextManager.createEntrySpan(name, contextCarrier);
            span.setComponent(new OfficialComponent(componentId, component));
            tag(tags);
            SpanLayer.asRPCFramework(span);
        }
    }
}
