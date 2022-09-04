package com.lxb.extension;



/**
 * 用于扩展点加载的时候描述扩展点信息
 */
public class Plugin<T> {
    /**
     * 实例名称
     */
    protected Name<T, String> name;
    /**
     * 构造器
     */
    protected Instantiation instantiation;
    /**
     * 是否是单例
     */
    protected Boolean singleton;
    /**
     * 扩展实现单例
     */
    protected T target;
    /**
     * 扩展点加载器
     */
    protected Object loader;

    public Plugin() {
    }

    public Plugin(Name<T, String> name, T target, Object loader) {
        this.name = name;
        this.target = target;
        this.loader = loader;
    }

    public Plugin(Name<T, String> name, Instantiation instantiation, Boolean singleton, T target, Object loader) {
        this.name = name;
        this.instantiation = instantiation;
        this.singleton = singleton;
        this.target = target;
        this.loader = loader;
    }

    public Name<T, String> getName() {
        return name;
    }

    public void setName(Name<T, String> name) {
        this.name = name;
    }

    public Boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(Boolean singleton) {
        this.singleton = singleton;
    }

    public T getTarget() {
        return target;
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

    public Object getLoader() {
        return loader;
    }

    public void setLoader(Object loader) {
        this.loader = loader;
    }
}
