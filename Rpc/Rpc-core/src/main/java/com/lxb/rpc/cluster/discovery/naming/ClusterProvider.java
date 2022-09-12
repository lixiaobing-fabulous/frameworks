package com.lxb.rpc.cluster.discovery.naming;


import com.lxb.extension.URL;
import com.lxb.rpc.cluster.Shard;
import java.util.List;
import java.util.function.BiFunction;

/**
 * 根据目录地址和参数提供集群信息
 */
@FunctionalInterface
public interface ClusterProvider extends BiFunction<URL, URL, List<Shard>> {
}
