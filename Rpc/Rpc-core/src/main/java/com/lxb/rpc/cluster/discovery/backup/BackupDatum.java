package com.lxb.rpc.cluster.discovery.backup;


import com.lxb.extension.URL;
import com.lxb.rpc.cluster.Shard;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 备份数据
 */
@Data
public class BackupDatum implements Serializable {

    private static final long                             serialVersionUID = 6912085093846011589L;
    /**
     * 集群信息
     */
    protected            Map<String, List<BackupShard>>   clusters;
    /**
     * 配置信息
     */
    protected            Map<String, Map<String, String>> configs;

    public BackupDatum() {
    }

    public BackupDatum(final Map<String, List<Shard>> snapshot) {
        if (snapshot != null) {
            clusters = new HashMap<>(snapshot.size());
            snapshot.forEach((k, v) -> {
                List<BackupShard> shards = new ArrayList<>(v.size());
                for (Shard shard : v) {
                    shards.add(new BackupShard(shard));
                }
                clusters.put(k, shards);
            });
        }
    }

    public Map<String, List<BackupShard>> getClusters() {
        return clusters;
    }

    public void setClusters(Map<String, List<BackupShard>> clusters) {
        this.clusters = clusters;
    }

    public Map<String, Map<String, String>> getConfigs() {
        return configs;
    }

    public void setConfigs(Map<String, Map<String, String>> configs) {
        this.configs = configs;
    }

    /**
     * 转换成快照
     *
     * @return
     */
    public Map<String, List<Shard>> toSnapshot() {
        Map<String, List<Shard>> result = new HashMap<>(clusters == null ? 0 : clusters.size());
        if (clusters != null) {
            clusters.forEach((k, v) -> {
                List<Shard> shards = new ArrayList<>(v.size());
                v.forEach(o -> {
                    shards.add(new Shard.DefaultShard(o.getName(), o.getRegion(), o.getDataCenter(),
                            o.getProtocol(), URL.valueOf(o.getAddress()), o.getWeight(), Shard.ShardState.INITIAL));
                });
                result.put(k, shards);
            });
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BackupDatum datum = (BackupDatum) o;

        if (clusters != null ? !clusters.equals(datum.clusters) : datum.clusters != null) {
            return false;
        }
        return configs != null ? configs.equals(datum.configs) : datum.configs == null;
    }

}
