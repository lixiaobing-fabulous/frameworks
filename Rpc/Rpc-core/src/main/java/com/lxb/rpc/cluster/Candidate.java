package com.lxb.rpc.cluster;


import java.util.List;

/**
 * 集群节点候选推荐,集群候选快照
 */
public class Candidate implements Region {
    //集群
    protected Cluster cluster;
    //当前区域
    protected Region region;
    //节点
    protected List<Node> nodes;
    //需要的条数
    protected int size;

    /**
     * 构造函数
     *
     * @param cluster
     * @param region
     * @param nodes
     * @param size
     */
    public Candidate(final Cluster cluster, final Region region, final List<Node> nodes, final int size) {
        this.cluster = cluster;
        this.region = region == null && cluster != null ? cluster.getRegion() : region;
        this.nodes = nodes == null && cluster != null ? cluster.getNodes() : nodes;
        this.size = size;
    }

    /**
     * 构造函数
     *
     * @param candidate
     * @param nodes
     */
    public Candidate(final Candidate candidate, final List<Node> nodes) {
        this.cluster = candidate.cluster;
        this.region = candidate.region;
        this.nodes = nodes;
        this.size = candidate.size;
    }

    public Cluster getCluster() {
        return cluster;
    }

    @Override
    public String getRegion() {
        return region.getRegion();
    }

    @Override
    public String getDataCenter() {
        return region.getDataCenter();
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public int getSize() {
        return size;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 构造器
     */
    public static class Builder {
        /**
         * 集群
         */
        protected Cluster cluster;
        /**
         * 地域机房
         */
        protected Region region;
        /**
         * 分片
         */
        protected List<Node> nodes;
        /**
         * 大小
         */
        protected int size;

        public Builder cluster(Cluster cluster) {
            this.cluster = cluster;
            return this;
        }

        public Builder region(Region region) {
            this.region = region;
            return this;
        }

        public Builder nodes(List<Node> nodes) {
            this.nodes = nodes;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Candidate build() {
            return new Candidate(cluster, region, nodes, size);
        }
    }

}
