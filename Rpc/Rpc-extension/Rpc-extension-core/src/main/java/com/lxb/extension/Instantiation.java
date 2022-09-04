package com.lxb.extension;

/**
 * 扩展实现构造器
 */
@FunctionalInterface
public interface Instantiation {

    /**
     * 构建实例
     *
     * @param name 实例名称
     * @param <T>
     * @return 实例对象
     */
    <T, M> T newInstance(Name<T, M> name);

    /**
     * 采用类的newInstance进行构造
     */
    class ClazzInstance implements Instantiation {

        public static final Instantiation INSTANCE = new ClazzInstance();

        @Override
        public <T, M> T newInstance(final Name<T, M> name) {
            try {
                return name == null ? null : name.getClazz().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                return null;
            }
        }
    }

}
