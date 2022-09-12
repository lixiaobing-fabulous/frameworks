package com.lxb.rpc.serialization.protostuff;


import com.lxb.extension.Extension;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.codec.serialization.Serialization;

/**
 * Proto，兼容grpc调用的content-type:application/grpc+proto
 */
@Extension(value = "proto", provider = "protostuff", order = Serialization.ORDER_PROTOBUF)
@ConditionalOnClass("io.protostuff.runtime.RuntimeSchema")
public class ProtoSerialization extends ProtobufSerialization {

}
