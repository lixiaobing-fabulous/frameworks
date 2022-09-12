package com.lxb.rpc.cluster.candidate.region;


import com.lxb.rpc.cluster.Node;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import static com.lxb.rpc.cluster.Shard.ShardState.*;

/**
 * 数据中心分布
 */
public class DataCenterDistribution {
    //区域
    protected String region;
    //机房
    protected String dataCenter;
    //本机房分片数量
    protected int              size;
    //本机房首选分片
    protected LinkedList<Node> high   = new LinkedList<>();
    //本机房未选择过的分片
    protected LinkedList<Node> normal = new LinkedList<>();
    //本机房不好的分片
    protected LinkedList<Node> low = new LinkedList<>();

    /**
     * 构造函数
     *
     * @param region
     * @param dataCenter
     */
    public DataCenterDistribution(String region, String dataCenter) {
        this.region = region;
        this.dataCenter = dataCenter;
    }

    /**
     * 添加节点
     *
     * @param node
     * @return
     */
    public boolean add(Node node) {
        boolean result;
        switch (node.getState()) {
            case CONNECTED:
            case CONNECTING:
            case WEAK:
            case CANDIDATE:
                result = high.add(node);
                break;
            case INITIAL:
                result = normal.add(node);
                break;
            default:
                result = low.add(node);
                break;
        }
        if (result) {
            size++;
        }
        return result;
    }

    /**
     * 选择候选者
     *
     * @param candidates 选择的节点
     * @param backups    备份节点
     * @param source     原始节点
     * @param count      数量
     * @param sorter     排序器
     * @return
     */
    protected int candidate(final List<Node> candidates, final List<Node> backups, final List<Node> source,
                            final int count, final Consumer<List<Node>> sorter) {
        if (count <= 0) {
            backups.addAll(source);
            return 0;
        } else {
            int size = source.size();
            if (size <= count) {
                candidates.addAll(source);
                return size;
            } else {
                if (sorter != null) {
                    sorter.accept(source);
                }
                candidates.addAll(source.subList(0, count));
                backups.addAll(source.subList(count, size));
                return count;
            }
        }
    }

    /**
     * 选择候选者
     *
     * @param candidates 选择的节点
     * @param backups    冷备节点
     * @param count      数量
     * @return 添加的数量
     */
    public int candidate(final List<Node> candidates, final List<Node> backups, final int count) {
        int remain = count - candidate(candidates, backups, high, count, o -> o.sort(null));
        remain -= candidate(candidates, backups, normal, remain, o -> Collections.shuffle(o));
        remain -= candidate(candidates, backups, low, remain, o -> Collections.shuffle(o));
        return count - remain;
    }

    public String getRegion() {
        return region;
    }

    public String getDataCenter() {
        return dataCenter;
    }

    public int getSize() {
        return size;
    }
}
