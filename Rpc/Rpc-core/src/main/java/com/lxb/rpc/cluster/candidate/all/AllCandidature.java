package com.lxb.rpc.cluster.candidate.all;


import com.lxb.extension.Extension;
import com.lxb.extension.URL;
import com.lxb.rpc.cluster.Candidate;
import com.lxb.rpc.cluster.candidate.Candidature;

/**
 * 全部生效，适用于数据节点
 */
@Extension("all")
public class AllCandidature implements Candidature {

    @Override
    public Result candidate(final URL url, final Candidate candidate) {
        return new Result(candidate.getNodes());
    }
}
