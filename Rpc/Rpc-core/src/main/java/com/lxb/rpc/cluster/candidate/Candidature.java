package com.lxb.rpc.cluster.candidate;


import com.lxb.extension.Extensible;
import com.lxb.extension.URL;
import com.lxb.rpc.cluster.Candidate;
import com.lxb.rpc.cluster.Node;

import java.util.LinkedList;
import java.util.List;


/**
 * 集群候选节点推荐逻辑
 */
@Extensible("candidature")
public interface Candidature {

    /**
     * Recommend candidate.
     *
     * @param url       URL
     * @param candidate 候选者
     */
    Result candidate(URL url, Candidate candidate);

    /**
     * 选择结果
     */
    class Result {

        //选择的节点
        protected List<Node> candidates;
        //热备的节点，权重为0
        protected List<Node> standbys;
        //冷备的节点
        protected List<Node> backups;
        //丢弃的节点
        protected List<Node> discards;

        public Result(List<Node> candidates) {
            this(candidates, null, null, null);
        }

        public Result(List<Node> candidates, List<Node> backups) {
            this(candidates, backups, null, null);
        }

        public Result(List<Node> candidates, List<Node> standbys, List<Node> backups, List<Node> discards) {
            this.candidates = candidates == null ? new LinkedList<>() : candidates;
            this.standbys = standbys == null ? new LinkedList<>() : standbys;
            this.backups = backups == null ? new LinkedList<>() : backups;
            this.discards = discards == null ? new LinkedList<>() : discards;
        }

        public List<Node> getCandidates() {
            return candidates;
        }

        public List<Node> getStandbys() {
            return standbys;
        }

        public List<Node> getBackups() {
            return backups;
        }

        public List<Node> getDiscards() {
            return discards;
        }

        /**
         * 建连的节点
         *
         * @return
         */
        public int getSize() {
            return candidates.size() + standbys.size();
        }
    }

}
