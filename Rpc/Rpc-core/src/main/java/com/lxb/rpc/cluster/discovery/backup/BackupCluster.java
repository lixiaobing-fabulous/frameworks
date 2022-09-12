package com.lxb.rpc.cluster.discovery.backup;


import java.io.Serializable;
import java.util.List;

/**
 * 备份集群数据
 */
public class BackupCluster implements Serializable {

    private static final long serialVersionUID = 4418447400095441113L;
    /**
     * 集群名称
     */
    protected String name;
    /**
     * 集群分片
     */
    protected List<BackupShard> shards;

    public BackupCluster() {
    }

    public BackupCluster(String name, List<BackupShard> shards) {
        this.name = name;
        this.shards = shards;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BackupShard> getShards() {
        return shards;
    }

    public void setShards(List<BackupShard> shards) {
        this.shards = shards;
    }
}
