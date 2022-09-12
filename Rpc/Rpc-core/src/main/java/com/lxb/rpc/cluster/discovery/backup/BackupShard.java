package com.lxb.rpc.cluster.discovery.backup;



import com.lxb.extension.URL;
import com.lxb.rpc.cluster.Shard;

import java.io.Serializable;

/**
 * 存储的分片信息
 */
public class BackupShard implements Serializable {
    private static final long serialVersionUID = -1479600922503780968L;
    public static final String NAME = "name";
    public static final String REGION = "region";
    public static final String DATA_CENTER = "dataCenter";
    public static final String PROTOCOL = "protocol";
    public static final String ADDRESS = "address";
    public static final String WEIGHT = "weight";
    /**
     * The Name.
     */
    protected String name;
    /**
     * The Region.
     */
    protected String region;
    /**
     * The Data center.
     */
    protected String dataCenter;
    /**
     * The Protocol.
     */
    protected String protocol;
    /**
     * The Address.
     */
    protected String address;
    /**
     * The Weight.
     */
    protected int weight;

    public BackupShard() {
    }

    public BackupShard(Shard shard) {
        this.name = shard.getName();
        this.region = shard.getRegion();
        this.dataCenter = shard.getDataCenter();
        this.protocol = shard.getProtocol();
        this.weight = shard.getWeight();
        this.address = shard.getUrl().toString();
    }

    public BackupShard(String name, String region, String dataCenter, String protocol, String address, int weight) {
        this.name = name;
        this.region = region;
        this.dataCenter = dataCenter;
        this.protocol = protocol;
        this.address = address;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDataCenter() {
        return dataCenter;
    }

    public void setDataCenter(String dataCenter) {
        this.dataCenter = dataCenter;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * 转换成Shard
     *
     * @return
     */
    public Shard toShard() {
        return new Shard.DefaultShard(name, region, dataCenter, protocol,
                address != null && !address.isEmpty() ? URL.valueOf(address) : null,
                weight, Shard.ShardState.INITIAL);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BackupShard shard = (BackupShard) o;

        if (weight != shard.weight) {
            return false;
        }
        if (name != null ? !name.equals(shard.name) : shard.name != null) {
            return false;
        }
        if (region != null ? !region.equals(shard.region) : shard.region != null) {
            return false;
        }
        if (dataCenter != null ? !dataCenter.equals(shard.dataCenter) : shard.dataCenter != null) {
            return false;
        }
        if (protocol != null ? !protocol.equals(shard.protocol) : shard.protocol != null) {
            return false;
        }

        if (address != null ? !address.equals(shard.address) : shard.address != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
