package com.lxb.rpc.fastjson;


import com.alibaba.fastjson.serializer.AutowiredObjectSerializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.lxb.rpc.cluster.discovery.backup.BackupShard;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * 备份序列化，加快序列化性能
 */
public class BackupShardSerializer extends AbstractSerializer implements AutowiredObjectSerializer {

    public static final BackupShardSerializer INSTANCE = new BackupShardSerializer();

    @Override
    public Set<Type> getAutowiredFor() {
        Set<Type> result = new HashSet<>(1);
        result.add(BackupShard.class);
        return result;
    }

    @Override
    public void write(final JSONSerializer serializer, final Object object, final Object fieldName, final Type fieldType, final int features) throws IOException {
        if (object == null) {
            serializer.writeNull();
        } else {
            SerializeWriter out = serializer.getWriter();
            out.write('{');
            BackupShard backupShard = (BackupShard) object;
            writeString(out, BackupShard.NAME, backupShard.getName());
            writeString(out, BackupShard.REGION, backupShard.getRegion());
            writeString(out, BackupShard.DATA_CENTER, backupShard.getDataCenter());
            writeString(out, BackupShard.PROTOCOL, backupShard.getProtocol());
            writeString(out, BackupShard.ADDRESS, backupShard.getAddress());
            out.writeFieldName(BackupShard.WEIGHT);
            out.writeInt(backupShard.getWeight());
            out.write('}');
        }
    }
}
