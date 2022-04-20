package com.lxb.cache.jedis;

import static redis.clients.jedis.params.SetParams.setParams;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import com.lxb.cache.cache.AbstractCache;
import com.lxb.cache.cache.ExpirableEntry;
import com.lxb.cache.config.Configuration;
import com.lxb.serialize.Deserializer;
import com.lxb.serialize.Deserializers;
import com.lxb.serialize.Serializer;
import com.lxb.serialize.Serializers;

import redis.clients.jedis.Jedis;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-04-03
 */
public class JedisCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {
    private final Jedis jedis;

    private final Serializers serializers;

    private final Deserializers deserializers;

    private final byte[] keyPrefixBytes;

    private final int keyPrefixBytesLength;

    public JedisCache(JedisCacheManager jedisCacheManager, String cacheName,
                      Configuration<K, V> configuration, Jedis jedis) {
        super(jedisCacheManager, cacheName, configuration);
        this.jedis = jedis;
        this.serializers = jedisCacheManager.getSerializers();
        this.deserializers = jedisCacheManager.getDeserializers();
        this.keyPrefixBytes = buildKeyPrefixBytes(cacheName);
        this.keyPrefixBytesLength = keyPrefixBytes.length;


    }

    private byte[] buildKeyPrefixBytes(String cacheName) {
        StringBuilder keyPrefixBuilder = new StringBuilder("JedisCache-")
                .append(cacheName).append(":");
        return keyPrefixBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }


    @Override
    protected ExpirableEntry<K, V> removeEntry(K key) {
        byte[] keyBytes = getKeyBytes(key);
        ExpirableEntry<K, V> oldEntry = getEntry(keyBytes);
        jedis.del(keyBytes);
        return oldEntry;
    }

    @Override
    protected boolean containsEntry(K key) {
        byte[] keyBytes = getKeyBytes(key);
        return jedis.exists(keyBytes);
    }

    @Override
    protected ExpirableEntry<K, V> getEntry(K key) {
        byte[] keyBytes = getKeyBytes(key);
        return getEntry(keyBytes);
    }

    protected ExpirableEntry<K, V> getEntry(byte[] keyBytes) throws ClassCastException {
        byte[] valueBytes = jedis.get(keyBytes);
        return deserialize(valueBytes, ExpirableEntry.class);
    }

    private <T> T deserialize(byte[] bytes, Class<T> deserializedType) {
        Deserializer deserializer = deserializers.getMostCompatible(deserializedType);
        return (T) deserializer.deserialize(bytes);
    }

    private byte[] serialize(Object value) {
        Serializer serializer = serializers.getMostCompatible(value.getClass());
        return serializer.serialize(value);
    }

    @Override
    protected Set<K> keySet() {
        Set<byte[]> keysBytes = jedis.keys(keyPrefixBytes);
        Set<K> keys = new LinkedHashSet<>(keysBytes.size());
        for (byte[] keyBytes : keysBytes) {
            keys.add(deserialize(keyBytes, getConfiguration().getKeyType()));
        }
        return Collections.unmodifiableSet(keys);
    }

    @Override
    protected void putEntry(ExpirableEntry<K, V> entry) {
        byte[] keyBytes = getKeyBytes(entry.getKey());
        byte[] valueBytes = serialize(entry);
        if (entry.isEternal()) {
            jedis.set(keyBytes, valueBytes);
        } else {
            jedis.set(keyBytes, valueBytes, setParams().px(entry.getExpiredTime()));
        }

    }

    @Override
    protected void clearEntries() {
        Set<byte[]> keysBytes = jedis.keys(keyPrefixBytes);
        for (byte[] keyBytes : keysBytes) {
            jedis.del(keyBytes);
        }

    }

    private byte[] getKeyBytes(Object key) {
        byte[] suffixBytes = serialize(key);
        int suffixBytesLength = suffixBytes.length;
        byte[] bytes = new byte[keyPrefixBytesLength + suffixBytesLength];
        System.arraycopy(keyPrefixBytes, 0, bytes, 0, keyPrefixBytesLength);
        System.arraycopy(suffixBytes, 0, bytes, keyPrefixBytesLength, suffixBytesLength);
        return bytes;
    }

    @Override
    protected void doClose() {
        this.jedis.close();
    }

}


