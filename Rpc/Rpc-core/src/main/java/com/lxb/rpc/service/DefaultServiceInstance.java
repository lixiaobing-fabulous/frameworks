package com.lxb.rpc.service;

import java.util.Map;

public class DefaultServiceInstance implements ServiceInstance {

    private String id;

    private String serviceName;

    private String host;

    private int port;

    private Map<String, String> metadata;

    @Override
    public String getId() {
        if (id == null) {
            return getHost() + "-" + getPort();
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
