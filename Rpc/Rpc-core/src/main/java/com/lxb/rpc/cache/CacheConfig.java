package com.lxb.rpc.cache;


/**
 * 缓存配置
 */
public class CacheConfig<K, V> {
    //键类型
    protected Class<K> keyClass;
    //值类型
    protected Class<V> valueClass;
    //最大数量
    protected int capacity = -1;
    //过期时间
    protected long expireAfterWrite = -1;
    //是否缓存空值
    protected boolean nullable;

    public CacheConfig() {
    }

    public CacheConfig(Class<K> keyClass, Class<V> valueClass, int capacity, long expireAfterWrite, boolean nullable) {
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        this.capacity = capacity;
        this.expireAfterWrite = expireAfterWrite;
        this.nullable = nullable;
    }

    public Class<K> getKeyClass() {
        return keyClass;
    }

    public void setKeyClass(Class<K> keyClass) {
        this.keyClass = keyClass;
    }

    public Class<V> getValueClass() {
        return valueClass;
    }

    public void setValueClass(Class<V> valueClass) {
        this.valueClass = valueClass;
    }

    public long getExpireAfterWrite() {
        return expireAfterWrite;
    }

    public void setExpireAfterWrite(long expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<>();
    }

    /**
     * 构建器
     *
     * @param <K>
     * @param <V>
     */
    public static class Builder<K, V> {

        //键类型
        protected Class<K> keyClass;
        //值类型
        protected Class<V> valueClass;
        //最大数量
        protected int capacity = -1;
        //过期时间
        protected long expireAfterWrite = -1;
        //是否缓存空值
        protected boolean nullable;

        public Builder<K, V> keyClass(final Class<K> keyClass) {
            this.keyClass = keyClass;
            return this;
        }

        public Builder<K, V> valueClass(final Class<V> valueClass) {
            this.valueClass = valueClass;
            return this;
        }

        public Builder<K, V> capacity(final int capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder<K, V> expireAfterWrite(final int expireAfterWrite) {
            this.expireAfterWrite = expireAfterWrite;
            return this;
        }

        public Builder<K, V> nullable(boolean nullable) {
            this.nullable = nullable;
            return this;
        }

        /**
         * 构建
         *
         * @return
         */
        public CacheConfig<K, V> build() {
            return new CacheConfig<>(keyClass, valueClass, capacity, expireAfterWrite, nullable);
        }

    }
}
