package com.lxb.rpc.codec.serialization;


import com.lxb.extension.Extensible;
import com.lxb.rpc.codec.CodecType;

/**
 * 对象序列化和反序列化提供者
 */
@Extensible("serialization")
public interface Serialization extends CodecType {

    int HESSIAN_ID = 2;

    int JAVA_ID = 3;

    /**
     * 兼容老版本的JSON输出格式
     */
    @Deprecated
    int JSON0_ID = 5;

    int KRYO_ID = 8;

    /**
     * 兼容老版本的JSON输出格式
     */
    @Deprecated
    int MSGPACK_ID = 10;

    int PROTOBUF_ID = 12;

    /**
     * 新的JSON格式
     */
    int JSON_ID = 13;

    int PROTOSTUFF_ID = 14;

    @Deprecated
    int HESSIAN_LITE_ID = 15;

    int MESSAGEPACK_ID = 16;

    int FST_ID = 17;

    int XML_ID = 100;

    int ADVANCE_JAVA_ID = 30;

    int DUBBO_PROTOSTUFF_ID = 31;

    int DUBBO_HESSIAN_ID = 32;

    int ORDER_PROTOSTUFF = 100;

    int ORDER_DUBBO_PROTOSTUFF = ORDER_PROTOSTUFF + 1;

    int ORDER_HESSIAN_LITE = ORDER_PROTOSTUFF + 10;

    int ORDER_HESSIAN = ORDER_HESSIAN_LITE + 10;

    int ORDER_DUBBO_HESSIAN = ORDER_HESSIAN + 1;

    int ORDER_DSLJSON = ORDER_HESSIAN + 10;

    int ORDER_FASTJSON = ORDER_DSLJSON + 10;

    int ORDER_JACKSON = ORDER_FASTJSON + 10;

    int ORDER_JAVA = ORDER_JACKSON + 10;

    int ORDER_ADVANCE_JAVA = ORDER_JAVA + 1;

    int ORDER_FST = ORDER_JAVA + 10;

    int ORDER_KRYO = ORDER_FST + 10;

    int ORDER_MESSAGEPACK = ORDER_KRYO + 10;

    int ORDER_PROTOBUF = ORDER_MESSAGEPACK + 10;

    int ORDER_JPROTOBUF = ORDER_PROTOBUF + 10;

    int ORDER_JAXB = ORDER_PROTOSTUFF + 300;

    /**
     * 获取内容格式
     *
     * @return
     */
    String getContentType();

    /**
     * 构建序列化器
     *
     * @return
     */
    Serializer getSerializer();

    /**
     * 是否自动识别类型信息
     *
     * @return 支持自动识别类型
     */
    default boolean autoType() {
        return true;
    }

}