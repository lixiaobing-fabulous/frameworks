package com.lxb.rpc.fastjson;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.CalendarCodec;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.lxb.extension.Extension;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.cluster.discovery.backup.BackupShard;
import com.lxb.rpc.codec.serialization.Json;
import com.lxb.rpc.codec.serialization.Serialization;
import com.lxb.rpc.codec.serialization.Serializer;
import com.lxb.rpc.codec.serialization.TypeReference;
import com.lxb.rpc.exception.SerializerException;
import com.lxb.rpc.fastjson.java8.MonthDaySerialization;
import com.lxb.rpc.fastjson.java8.YearMonthSerialization;
import com.lxb.rpc.fastjson.java8.YearSerialization;
import com.lxb.rpc.fastjson.java8.ZoneIdSerialization;
import com.lxb.rpc.fastjson.java8.ZoneOffsetSerialization;
import com.lxb.rpc.protocol.message.Invocation;
import com.lxb.rpc.protocol.message.ResponsePayload;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.alibaba.fastjson.JSON.DEFAULT_GENERATE_FEATURE;
import static com.lxb.rpc.context.Variable.VARIABLE;

/**
 * JSON序列化，不推荐在调用请求序列化场景使用
 */
@Extension(value = "json", provider = "fastjson", order = Serialization.ORDER_FASTJSON)
@ConditionalOnClass("com.alibaba.fastjson.JSON")
public class JsonSerialization implements Serialization, Json {

    @Override
    public byte getTypeId() {
        return JSON_ID;
    }

    @Override
    public String getContentType() {
        return "text/json";
    }

    @Override
    public boolean autoType() {
        //在序列化Invocation的调用参数时候不支持类型，需要类名
        return false;
    }

    @Override
    public Serializer getSerializer() {
        return JsonSerializer.INSTANCE;
    }

    @Override
    public void writeJSONString(final OutputStream os, final Object object) throws SerializerException {
        JsonSerializer.INSTANCE.writeJSONString(os, object);
    }

    @Override
    public String toJSONString(final Object object) throws SerializerException {
        return JsonSerializer.INSTANCE.toJSONString(object);
    }

    @Override
    public byte[] toJSONBytes(final Object object) throws SerializerException {
        return JsonSerializer.INSTANCE.toJSONBytes(object);
    }

    @Override
    public <T> T parseObject(final String text, final Type type) throws SerializerException {
        return JsonSerializer.INSTANCE.parseObject(text, type);
    }

    @Override
    public <T> T parseObject(final String text, final TypeReference<T> reference) throws SerializerException {
        return JsonSerializer.INSTANCE.parseObject(text, reference);
    }

    @Override
    public <T> T parseObject(final InputStream is, final Type type) throws SerializerException {
        return JsonSerializer.INSTANCE.parseObject(is, type);
    }

    @Override
    public <T> T parseObject(final InputStream is, final TypeReference<T> reference) throws SerializerException {
        return JsonSerializer.INSTANCE.parseObject(is, reference);
    }

    @Override
    public void parseArray(final Reader reader, final Function<Function<Type, Object>, Boolean> function) throws SerializerException {
        JsonSerializer.INSTANCE.parseArray(reader, function);
    }

    @Override
    public void parseObject(final Reader reader, final BiFunction<String, Function<Type, Object>, Boolean> function) throws SerializerException {
        JsonSerializer.INSTANCE.parseObject(reader, function);
    }


    /**
     * JSON序列化和反序列化实现，有多种JSON序列化框架，把Fastjson的异常进行转换
     */
    protected static class JsonSerializer implements Serializer, Json {


        protected static final JsonSerializer INSTANCE = new JsonSerializer();

        protected JsonConfig parserConfig;
        protected SerializeConfig serializeConfig;
        protected Feature[] parserFeatures;
        protected SerializerFeature[] serializerFeatures;

        protected JsonSerializer() {
            serializeConfig = createSerializeConfig();
            parserConfig = createParserConfig();
            parserFeatures = createParserFeatures();
            serializerFeatures = createSerializerFeatures();
        }

        /**
         * 创建序列化配置
         *
         * @return
         */
        protected SerializeConfig createSerializeConfig() {
            //不采用全局配置，防止用户修改，造成消费者处理错误
            SerializeConfig config = new SerializeConfig();
            config.put(MonthDay.class, MonthDaySerialization.INSTANCE);
            config.put(YearMonth.class, YearMonthSerialization.INSTANCE);
            config.put(Year.class, YearSerialization.INSTANCE);
            config.put(ZoneOffset.class, ZoneOffsetSerialization.INSTANCE);
            config.put(ZoneId.class, ZoneIdSerialization.INSTANCE);
            config.put(ZoneId.systemDefault().getClass(), ZoneIdSerialization.INSTANCE);
            config.put(Invocation.class, InvocationCodec.INSTANCE);
            config.put(ResponsePayload.class, ResponsePayloadCodec.INSTANCE);
            config.put(BackupShard.class, BackupShardSerializer.INSTANCE);
            return config;
        }

        /**
         * 构造反序列化配置
         *
         * @return
         */
        protected JsonConfig createParserConfig() {
            JsonConfig config = new JsonConfig();
            //白名单模式，默认关闭安全模式
            //config.setSafeMode(VARIABLE.getBoolean(ParserConfig.SAFE_MODE_PROPERTY, true));
            config.putDeserializer(MonthDay.class, MonthDaySerialization.INSTANCE);
            config.putDeserializer(YearMonth.class, YearMonthSerialization.INSTANCE);
            config.putDeserializer(Year.class, YearSerialization.INSTANCE);
            config.putDeserializer(ZoneOffset.class, ZoneOffsetSerialization.INSTANCE);
            config.putDeserializer(ZoneId.class, ZoneIdSerialization.INSTANCE);
            config.putDeserializer(ZoneId.systemDefault().getClass(), ZoneIdSerialization.INSTANCE);
            config.putDeserializer(Invocation.class, InvocationCodec.INSTANCE);
            config.putDeserializer(ResponsePayload.class, ResponsePayloadCodec.INSTANCE);
            config.putDeserializer(GregorianCalendar.class, CalendarCodec.instance);
            return config;
        }

        /**
         * 添加解析Feature
         *
         * @param features
         */
        protected void addParserFeature(Set<Feature> features) {

        }

        /**
         * 构造反序列化特征
         *
         * @return
         */
        protected Feature[] createParserFeatures() {
            HashSet<Feature> set = new HashSet<>();
            //从上下文中读取
            String cfg = VARIABLE.getString("json.parser.features");
            if (cfg != null && !cfg.isEmpty()) {
                String[] features = cfg.split("[,;\\s]");
                for (String feature : features) {
                    try {
                        set.add(Feature.valueOf(feature));
                    } catch (IllegalArgumentException e) {
                    }
                }

            }
            addParserFeature(set);
            if (!set.isEmpty()) {
                return set.toArray(new Feature[set.size()]);
            }
            return new Feature[0];
        }

        /**
         * 添加序列化Feature
         *
         * @param features
         */
        protected void addSerializerFeature(Set<SerializerFeature> features) {
            features.add(SerializerFeature.WriteNonStringKeyAsString);
        }

        /**
         * 构造序列化特征
         *
         * @return
         */
        protected SerializerFeature[] createSerializerFeatures() {
            HashSet<SerializerFeature> set = new HashSet<>();
            String cfg = VARIABLE.getString("json.serializer.features");
            if (cfg != null && !cfg.isEmpty()) {
                String[] features = cfg.split("[,;\\s]");
                for (String feature : features) {
                    try {
                        set.add(SerializerFeature.valueOf(feature));
                    } catch (IllegalArgumentException ignored) {
                    }
                }

            }
            addSerializerFeature(set);
            if (!set.isEmpty()) {
                return set.toArray(new SerializerFeature[set.size()]);
            }
            return new SerializerFeature[0];
        }

        @Override
        public <T> void serialize(final OutputStream os, final T object) throws SerializerException {
            try {
                JSON.writeJSONString(os, StandardCharsets.UTF_8, object, serializeConfig, null, null, DEFAULT_GENERATE_FEATURE, serializerFeatures);
            } catch (SerializerException e) {
                throw e;
            } catch (JSONException e) {
                throw new SerializerException("Error occurs while serializing object,caused by " + e.getMessage(),
                        e.getCause() != null ? e.getCause() : null);
            } catch (Exception e) {
                throw new SerializerException("Error occurs while serializing object,caused by " + e.getMessage(), e);
            }
        }

        @Override
        public <T> T deserialize(final InputStream is, final Type type) throws SerializerException {
            try {
                return (T) JSON.parseObject(is, StandardCharsets.UTF_8, type, parserConfig, parserFeatures);
            } catch (SerializerException e) {
                throw e;
            } catch (JSONException e) {
                throw new SerializerException("Error occurs while deserializing object,caused by " + e.getMessage(),
                        e.getCause() != null ? e.getCause() : null);
            } catch (Exception e) {
                throw new SerializerException("Error occurs while deserializing object,caused by " + e.getMessage(), e);
            }
        }

        @Override
        public void writeJSONString(final OutputStream os, final Object object) throws SerializerException {
            serialize(os, object);
        }

        @Override
        public String toJSONString(final Object object) throws SerializerException {
            try {
                return JSON.toJSONString(object, serializeConfig, serializerFeatures);
            } catch (SerializerException e) {
                throw e;
            } catch (JSONException e) {
                throw new SerializerException("Error occurs while serializing object,caused by " + e.getMessage(),
                        e.getCause() != null ? e.getCause() : null);
            } catch (Exception e) {
                throw new SerializerException("Error occurs while serializing object,caused by " + e.getMessage(), e);
            }
        }

        @Override
        public byte[] toJSONBytes(final Object object) throws SerializerException {
            try {
                return JSON.toJSONBytes(object, serializeConfig, serializerFeatures);
            } catch (SerializerException e) {
                throw e;
            } catch (JSONException e) {
                throw new SerializerException("Error occurs while serializing object,caused by " + e.getMessage(),
                        e.getCause() != null ? e.getCause() : null);
            } catch (Exception e) {
                throw new SerializerException("Error occurs while serializing object,caused by " + e.getMessage(), e);
            }
        }

        @Override
        public <T> T parseObject(final String text, final Type type) throws SerializerException {
            if (text == null) {
                return null;
            }
            try {
                return JSON.parseObject(text, type, parserConfig, parserFeatures);
            } catch (SerializerException e) {
                throw e;
            } catch (JSONException e) {
                throw new SerializerException("Error occurs while parsing object,caused by " + e.getMessage(),
                        e.getCause() != null ? e.getCause() : null);
            } catch (Exception e) {
                throw new SerializerException("Error occurs while parsing object,caused by " + e.getMessage(), e);
            }
        }

        @Override
        public <T> T parseObject(final InputStream is, final Type type) throws SerializerException {
            if (is == null) {
                return null;
            }
            try {
                return JSON.parseObject(is, StandardCharsets.UTF_8, type, parserConfig, parserFeatures);
            } catch (SerializerException e) {
                throw e;
            } catch (JSONException e) {
                throw new SerializerException("Error occurs while parsing object,caused by " + e.getMessage(),
                        e.getCause() != null ? e.getCause() : null);
            } catch (Exception e) {
                throw new SerializerException("Error occurs while parsing object,caused by " + e.getMessage(), e);
            }
        }

        @Override
        public <T> T parseObject(final InputStream is, final TypeReference<T> reference) throws SerializerException {
            if (is == null) {
                return null;
            }
            try {
                return JSON.parseObject(is, StandardCharsets.UTF_8, reference == null ? null : reference.getType(), parserConfig, parserFeatures);
            } catch (SerializerException e) {
                throw e;
            } catch (JSONException e) {
                throw new SerializerException("Error occurs while parsing object,caused by " + e.getMessage(),
                        e.getCause() != null ? e.getCause() : null);
            } catch (Exception e) {
                throw new SerializerException("Error occurs while parsing object,caused by " + e.getMessage(), e);
            }
        }

        @Override
        public <T> T parseObject(final String text, final TypeReference<T> reference) throws SerializerException {
            if (text == null) {
                return null;
            }
            try {
                return JSON.parseObject(text, reference == null ? null : reference.getType(), parserConfig, parserFeatures);
            } catch (SerializerException e) {
                throw e;
            } catch (JSONException e) {
                throw new SerializerException("Error occurs while parsing object,caused by " + e.getMessage(),
                        e.getCause() != null ? e.getCause() : null);
            } catch (Exception e) {
                throw new SerializerException("Error occurs while parsing object,caused by " + e.getMessage(), e);
            }
        }

        @Override
        public void parseArray(final Reader reader, final Function<Function<Type, Object>, Boolean> function) throws SerializerException {
            if (reader == null || function == null) {
                return;
            }

            try (JSONReader jsonReader = new JSONReader(reader, parserFeatures)) {
                jsonReader.startArray();
                while (jsonReader.hasNext()) {
                    if (!function.apply(o -> jsonReader.readObject(o))) {
                        break;
                    }
                }
                jsonReader.endArray();
            } catch (SerializerException e) {
                throw e;
            } catch (JSONException e) {
                throw new SerializerException("Error occurs while parsing object,caused by " + e.getMessage(),
                        e.getCause() != null ? e.getCause() : null);
            } catch (Exception e) {
                throw new SerializerException("Error occurs while parsing object,caused by " + e.getMessage(), e);
            }

        }

        @Override
        public void parseObject(final Reader reader, final BiFunction<String, Function<Type, Object>, Boolean> function) throws SerializerException {
            if (reader == null || function == null) {
                return;
            }

            try (JSONReader jsonReader = new JSONReader(reader, parserFeatures)) {
                jsonReader.startObject();
                while (jsonReader.hasNext()) {
                    if (!function.apply(jsonReader.readString(), o -> jsonReader.readObject(o))) {
                        break;
                    }
                }
                jsonReader.endObject();
            } catch (SerializerException e) {
                throw e;
            } catch (JSONException e) {
                throw new SerializerException("Error occurs while parsing object,caused by " + e.getMessage(),
                        e.getCause() != null ? e.getCause() : null);
            } catch (Exception e) {
                throw new SerializerException("Error occurs while parsing object,caused by " + e.getMessage(), e);
            }

        }
    }
}
