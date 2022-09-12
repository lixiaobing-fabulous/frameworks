package com.lxb.rpc.cluster;

import com.lxb.extension.URL;

public class Node implements Shard {
    /**
     * 集群URL
     */
    protected final URL    clusterUrl;
    /**
     * 集群名称
     */
    protected final String clusterName;
    /**
     * 分片
     */
    protected final Shard  shard;

    public Node(String clusterName, URL clusterUrl, Shard shard) {
        this.clusterUrl = clusterUrl;
        this.clusterName = clusterName;
        this.shard = shard;
    }

    @Override
    public String getDataCenter() {
        return shard.getDataCenter();
    }

    @Override
    public String getRegion() {
        return shard.getRegion();
    }

    @Override
    public String getName() {
        return shard.getName();
    }

    @Override
    public String getProtocol() {
        return shard.getProtocol();
    }

    @Override
    public URL getUrl() {
        return shard.getUrl();
    }

    @Override
    public int getWeight() {
        return shard.getWeight();
    }

    @Override
    public ShardState getState() {
        return shard.getState();
    }


}
