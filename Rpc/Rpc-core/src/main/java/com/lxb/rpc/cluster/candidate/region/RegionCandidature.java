package com.lxb.rpc.cluster.candidate.region;


import com.lxb.extension.Extension;
import com.lxb.extension.URL;
import com.lxb.rpc.cluster.Candidate;
import com.lxb.rpc.cluster.candidate.Candidature;

/**
 * 区域感知算法，优先本地机房，再考虑本区域
 */
@Extension("region")
public class RegionCandidature implements Candidature {

    @Override
    public Result candidate(final URL url, final Candidate candidate) {
        if (candidate == null) {
            return null;
        }

        RegionDistribution region = new RegionDistribution(candidate.getRegion(), candidate.getDataCenter(), candidate.getNodes(), url);
        return region.candidate(candidate.getSize());
    }

}
