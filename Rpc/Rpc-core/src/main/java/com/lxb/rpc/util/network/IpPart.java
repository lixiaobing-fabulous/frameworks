package com.lxb.rpc.util.network;


/**
 * IP分段
 */
public class IpPart {

    /**
     * 类型
     */
    protected IpType type;
    /**
     * 分段
     */
    protected int[] parts;
    /**
     * 网口名称
     */
    protected String ifName;

    public IpPart(IpType type, int[] parts) {
        this.type = type;
        this.parts = parts;
    }

    public IpPart(IpType type, int[] parts, String ifName) {
        this.type = type;
        this.parts = parts;
        this.ifName = ifName;
    }

    public IpType getType() {
        return type;
    }

    public int[] getParts() {
        return parts;
    }

    public String getIfName() {
        return ifName;
    }
}
