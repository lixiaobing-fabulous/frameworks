package com.lxb.extension;

import java.util.Comparator;

/**
 * 扩展点元数据
 */
public class ExtensionMeta<T, M> {

    /**
     * 扩展点元数据名称
     */
    protected Name<T, String> name;
    /**
     * 扩展点名称
     */
    protected Name<T, String> extensible;
    /**
     * 扩展实现名称
     */
    protected Name<T, M> extension;
    /**
     * 扩展点加载器
     */
    protected Object loader;
    /**
     * 扩展实现构造器
     */
    protected Instantiation instantiation;
    /**
     * 是否是单例
     */
    protected boolean singleton = true;
    /**
     * 顺序
     */
    protected int order;
    /**
     * 供应商
     */
    protected String provider;
    /**
     * 单例扩展实现
     */
    protected T target;

    public Name<T, String> getName() {
        return name;
    }

    public void setName(Name<T, String> name) {
        this.name = name;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    public T getTarget() {
        if (singleton) {
            if (target == null) {
                synchronized (this) {
                    if (target == null) {
                        target = instantiation.newInstance(name);
                    }
                }
            }
            return target;
        }
        return instantiation.newInstance(name);
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public Instantiation getInstantiation() {
        return instantiation;
    }

    public void setInstantiation(Instantiation instantiation) {
        this.instantiation = instantiation;
    }

    public Name<T, String> getExtensible() {
        return extensible;
    }

    public void setExtensible(Name<T, String> extensible) {
        this.extensible = extensible;
    }

    public Name<? extends T, M> getExtension() {
        return extension;
    }

    public void setExtension(Name<T, M> extension) {
        this.extension = extension;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Object getLoader() {
        return loader;
    }

    public void setLoader(Object loader) {
        this.loader = loader;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * 升序排序
     */
    static class AscendingComparator implements Comparator<ExtensionMeta<?, ?>> {

        protected static final AscendingComparator INSTANCE = new AscendingComparator();

        @Override
        public int compare(ExtensionMeta<?, ?> o1, ExtensionMeta<?, ?> o2) {
            return o1.getOrder() - o2.getOrder();
        }
    }

}
