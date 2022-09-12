package com.lxb.rpc.cluster.discovery.naming.fix;


import com.lxb.extension.MapParametric;
import com.lxb.extension.Parametric;
import com.lxb.extension.URL;
import com.lxb.rpc.cluster.Shard;
import com.lxb.rpc.cluster.discovery.naming.AbstractRegistar;
import com.lxb.rpc.cluster.discovery.naming.ClusterProvider;
import com.lxb.rpc.context.GlobalContext;
import com.lxb.rpc.exception.InitializationException;
import com.lxb.rpc.util.StringUtils;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import static com.lxb.rpc.cluster.Shard.WEIGHT;
import static com.lxb.rpc.constants.Constants.DEFAULT_PORT;
import static com.lxb.rpc.constants.Constants.PROTOCOL_KEY;
import static com.lxb.rpc.util.StringUtils.SEMICOLON_COMMA_WHITESPACE;

/**
 * 直连目录服务
 */
public class FixRegistar extends AbstractRegistar {

    protected static final String SHARD_KEY = "address";

    /**
     * 分片分隔符
     */
    protected Predicate<Character> delimiterPredicate;
    /**
     * 分片参数名称
     */
    protected String shardKey;

    public FixRegistar(final URL url) {
        this(url.getProtocol(), url, SEMICOLON_COMMA_WHITESPACE, SHARD_KEY);
    }

    public FixRegistar(final String name, final URL url) {
        this(name, url, SEMICOLON_COMMA_WHITESPACE, SHARD_KEY);
    }

    public FixRegistar(final String name, final URL url, final Predicate<Character> delimiterPredicate, final String shardKey) {
        super(name, url);
        this.delimiterPredicate = delimiterPredicate == null ? SEMICOLON_COMMA_WHITESPACE : delimiterPredicate;
        this.shardKey = shardKey == null || shardKey.isEmpty() ? SHARD_KEY : shardKey;
        this.provider = new URLProvider();
    }

    @Override
    public CompletableFuture<Void> open() {
        return switcher.openQuiet(() -> CompletableFuture.completedFuture(null));
    }

    @Override
    public CompletableFuture<Void> close() {
        return switcher.closeQuiet(() -> CompletableFuture.completedFuture(null));
    }

    @Override
    protected ClusterMeta create(final URL url, final String name) {
        //创建集群元数据，添加到任务队列
        ClusterMeta meta = new ClusterMeta(url, name);
        meta.setShards(provider.apply(this.url, url));
        return meta;
    }

    /**
     * 从URL参数获取集群节点
     */
    protected class URLProvider implements ClusterProvider {

        /**
         * 获取分片
         *
         * @param url
         * @return
         */
        protected List<Shard> apply(final URL url) {
            List<Shard> result = new LinkedList<>();
            String value = url == null ? null : url.getString(shardKey);
            if (value == null || value.isEmpty()) {
                value = url.getHost() + ":" + url.getPort();
            }
            String rg = url.getString(REGION, region);
            String dc = url.getString(DATA_CENTER, dataCenter);
            String name = url.getString("name");
            try {
                value = URL.decode(value);
            } catch (UnsupportedEncodingException ex) {
                throw new InitializationException("Value of \"url\" value is not encode in consumer config with key " + url + " !");
            }
            String[] shards = StringUtils.split(value, delimiterPredicate);
            int j = 0;
            URL        nodeUrl;
            Parametric parametric  = new MapParametric(GlobalContext.getContext());
            String     defProtocol = parametric.getString(PROTOCOL_KEY);
            Integer defPort = url.getPort() <= 0 ? DEFAULT_PORT : url.getPort();
            for (String shard : shards) {
                nodeUrl = URL.valueOf(shard, defProtocol, defPort, null);
                result.add(new Shard.DefaultShard(name != null ? name + "-" + j++ : nodeUrl.getAddress(),
                        rg, dc, nodeUrl.getProtocol(), nodeUrl,
                        nodeUrl.getInteger(WEIGHT), Shard.ShardState.INITIAL));
            }
            return result;
        }

        @Override
        public List<Shard> apply(URL endpoint, URL cluster) {
            List<Shard> result = null;
            if (endpoint != null) {
                result = apply(endpoint);
            }
            if (result == null || result.isEmpty()) {
                result = apply(cluster);
            }
            return result;
        }
    }

}
