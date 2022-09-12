package com.lxb.rpc.cluster.candidate.single;


import com.lxb.extension.Extension;
import com.lxb.extension.URL;
import com.lxb.rpc.cluster.Candidate;
import com.lxb.rpc.cluster.Node;
import com.lxb.rpc.cluster.candidate.Candidature;

import java.util.ArrayList;
import java.util.List;

/**
 * 连接主节点，适用于数据节点
 */
@Extension("single")
public class SingleCandidature implements Candidature {

    @Override
    public Result candidate(final URL url, final Candidate candidate) {
        List<Node> nodes = candidate.getNodes();
        int        size  = nodes.size();
        return new Result(size > 0 ? nodes.subList(0, 1) : new ArrayList<>(0),
                new ArrayList<>(0),
                size > 1 ? nodes.subList(1, nodes.size()) : new ArrayList<>(0),
                new ArrayList<>(0));
    }
}
