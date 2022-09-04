package com.lxb.rpc.proxy.bytebuddy;

import com.lxb.extension.Extension;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.exception.ProxyException;
import com.lxb.rpc.proxy.ProxyFactory;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;

/**
 * The type Byte buddy proxy factory.
 *
 * @date: 1 /23/2019
 */
@Extension("bytebuddy")
@ConditionalOnClass("net.bytebuddy.ByteBuddy")
public class ByteBuddyProxyFactory implements ProxyFactory {

    /**
     * The Byte buddy.
     */
    protected static final ByteBuddy BYTE_BUDDY = new ByteBuddy(ClassFileVersion.ofThisVm(ClassFileVersion.JAVA_V8));

    @Override
    public <T> T getProxy(final Class<T> clz, final InvocationHandler invoker, final ClassLoader classLoader) throws ProxyException {
        Class<? extends T> clazz = BYTE_BUDDY.subclass(clz)
                .method(new NoneStaticMatcher<>())
                .intercept(MethodDelegation.to(new ByteBuddyInvocationHandler(invoker)))
                .make()
                .load(classLoader, ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new ProxyException("Error occurred while creating bytebuddy proxy of " + clz.getName(), e);
        }
    }

    /**
     * 非静态方法匹配
     *
     * @param <T>
     */
    protected class NoneStaticMatcher<T extends ModifierReviewable> extends ElementMatcher.Junction.AbstractBase<T> {

        @Override
        public boolean matches(T target) {
            return !Modifier.isStatic(target.getModifiers());
        }

        @Override
        public String toString() {
            return "isNoneStatic()";
        }
    }

}