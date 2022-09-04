package com.lxb.extension;



import com.lxb.extension.listener.LoaderEvent;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * 延迟加载扩展点
 *
 * @param <T>
 * @param <M>
 */
public class ExtensionPointLazy<T, M> implements ExtensionPoint<T, M> {

    protected volatile ExtensionPoint<T, M> delegate;
    protected final Class<T> extensible;
    protected final ExtensionLoader loader;
    protected final Comparator<ExtensionMeta<?, ?>> comparator;
    protected final Classify<T, M> classify;

    public ExtensionPointLazy(Class<T> extensible) {
        this(extensible, null, null, null);
    }

    public ExtensionPointLazy(Class<T> extensible, Comparator<ExtensionMeta<?, ?>> comparator) {
        this(extensible, null, comparator, null);
    }

    public ExtensionPointLazy(Class<T> extensible, Classify<T, M> classify) {
        this(extensible, null, null, classify);
    }

    public ExtensionPointLazy(Class<T> extensible, ExtensionLoader loader, Comparator<ExtensionMeta<?, ?>> comparator,
                              Classify<T, M> classify) {
        this.extensible = extensible;
        this.loader = loader;
        this.comparator = comparator;
        this.classify = classify;
    }

    protected final ExtensionPoint<T, M> getDelegate() {
        if (delegate == null) {
            synchronized (this) {
                if (delegate == null) {
                    //监听扩展点加载器变更事件，需要重新获取插件
                    ExtensionManager.addListener(event -> {
                        if (event instanceof LoaderEvent) {
                            delegate = null;
                        }
                    });
                    delegate = ExtensionManager.getOrLoadExtensionPoint(extensible, loader, comparator, classify);
                }
            }
        }
        return delegate;
    }

    @Override
    public T get(final M name) {
        return getDelegate().get(name);
    }

    @Override
    public T get(final M name, final M option) {
        return getDelegate().get(name, option);
    }

    @Override
    public T getOrDefault(final M name) {
        return getDelegate().getOrDefault(name);
    }

    @Override
    public T get(M[] names) {
        return getDelegate().get(names);
    }

    @Override
    public T get() {
        return getDelegate().get();
    }

    @Override
    public Iterable<ExtensionMeta<T, M>> metas() {
        return getDelegate().metas();
    }

    @Override
    public Iterable<ExtensionMeta<T, M>> metas(final M name) {
        return getDelegate().metas(name);
    }

    @Override
    public ExtensionMeta<T, M> meta(final M name) {
        return getDelegate().meta(name);
    }

    @Override
    public int size() {
        return getDelegate().size();
    }

    @Override
    public Iterable<T> extensions() {
        return getDelegate().extensions();
    }

    @Override
    public Iterable<T> extensions(Predicate<T> predicate) {
        return getDelegate().extensions(predicate);
    }

    @Override
    public Iterable<T> reverse() {
        return getDelegate().reverse();
    }

    @Override
    public Name<T, String> getName() {
        return getDelegate().getName();
    }

    @Override
    public List<M> names() {
        return getDelegate().names();
    }

    @Override
    public List<M> available(final List<M> names) {
        return getDelegate().available(names);
    }
}
