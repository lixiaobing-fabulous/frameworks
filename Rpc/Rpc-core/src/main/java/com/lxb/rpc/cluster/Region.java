package com.lxb.rpc.cluster;


/**
 * 地域接口
 */
public interface Region {

    /**
     * 地域
     */
    String REGION = "region";
    /**
     * 数据中心（key值大小写敏感）
     */
    String DATA_CENTER = "datacenter";

    /**
     * 区域
     *
     * @return
     */
    String getRegion();

    /**
     * 数据中心
     *
     * @return
     */
    String getDataCenter();


    /**
     * Region默认实现
     */
    class DefaultRegion implements Region {
        /**
         * 区域
         */
        protected String region;
        /**
         * 数据中心
         */
        protected String dataCenter;

        /**
         * 构造函数
         *
         * @param region
         * @param dataCenter
         */
        public DefaultRegion(String region, String dataCenter) {
            this.region = region;
            this.dataCenter = dataCenter;
        }

        @Override
        public String getRegion() {
            return region;
        }

        @Override
        public String getDataCenter() {
            return dataCenter;
        }
    }
}
