package com.lxb.rpc.serealization;


import com.lxb.extension.ExtensionPoint;
import com.lxb.extension.ExtensionPointLazy;
import com.lxb.rpc.cluster.discovery.backup.BackupDatum;
import com.lxb.rpc.cluster.discovery.backup.BackupShard;
import com.lxb.rpc.codec.UnsafeByteArrayInputStream;
import com.lxb.rpc.codec.UnsafeByteArrayOutputStream;
import com.lxb.rpc.codec.serialization.Serialization;
import com.lxb.rpc.codec.serialization.Serializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class BackupTest {
    ExtensionPoint<Serialization, String> SERIALIZATION = new ExtensionPointLazy<>(Serialization.class);

    @Test
    public void testBackup() {

        Map<String, List<BackupShard>> clusters = new HashMap<>();
        List<BackupShard>              shards   = new LinkedList<>();
        shards.add(new BackupShard("test", null, null, "joyrpc", "joyrpc://192.168.1.1:22000", 100));
        clusters.put("test", shards);
        Map<String, Map<String, String>> configs = new HashMap<>();
        Map<String, String>              config  = new HashMap<>();
        config.put("socketTimeout", "10000");
        configs.put("test", config);

        BackupDatum datum = new BackupDatum();
        datum.setClusters(clusters);
        datum.setConfigs(configs);

        String[] types = new String[]{"fst", "hessian", "java", "json@fastjson", "json@jackson", "kryo", "protostuff"};

        Serialization               serialization;
        Serializer                  serializer;
        UnsafeByteArrayOutputStream baos  = new UnsafeByteArrayOutputStream(1024);
        UnsafeByteArrayInputStream  bais;
        for (int j = 0; j < types.length; j++) {
            serialization = SERIALIZATION.get(types[j]);
            serializer = serialization.getSerializer();

            baos.reset();
            serializer.serialize(baos, datum);
            bais = new UnsafeByteArrayInputStream(baos.toByteArray());
            BackupDatum data = serializer.deserialize(bais, BackupDatum.class);
            System.out.println(types[j]);
            System.out.println(data);
            Assertions.assertEquals(data, datum);
        }
    }

}
