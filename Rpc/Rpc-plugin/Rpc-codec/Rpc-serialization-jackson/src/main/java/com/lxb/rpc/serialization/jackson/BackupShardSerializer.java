package com.lxb.rpc.serialization.jackson;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.lxb.rpc.cluster.discovery.backup.BackupShard;

import java.io.IOException;

/**
 * 备份序列化，加快序列化性能
 */
public class BackupShardSerializer extends JsonSerializer<BackupShard> {

    public static final BackupShardSerializer INSTANCE = new BackupShardSerializer();

    @Override
    public void serialize(final BackupShard shard, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        if (shard == null) {
            gen.writeNull();
        } else {
            gen.writeStartObject();
            gen.writeStringField(BackupShard.NAME, shard.getName());
            gen.writeStringField(BackupShard.REGION, shard.getRegion());
            gen.writeStringField(BackupShard.DATA_CENTER, shard.getDataCenter());
            gen.writeStringField(BackupShard.PROTOCOL, shard.getProtocol());
            gen.writeStringField(BackupShard.ADDRESS, shard.getAddress());
            gen.writeNumberField(BackupShard.WEIGHT, shard.getWeight());
            gen.writeEndObject();
        }
    }
}
