package com.lxb.rpc.exception;


/**
 * 过载异常
 */
public class OverloadException extends RejectException {

    private static final long serialVersionUID = 8092542592823750863L;

    //期望降到的目标TPS
    protected int tps;

    protected boolean isServer;

    public OverloadException() {
        super(null, null, false, false, null, true);
    }

    public OverloadException(String message) {
        super(message, null, false, false, null, true);
    }

    public OverloadException(String message, int tps, boolean isServer) {
        super(message, null, false, false, null, true);
        this.tps = tps;
        this.isServer = isServer;
    }

    public OverloadException(String message, String errorCode, int tps, boolean isServer) {
        //不输出堆栈，减少限流造成的CPU过高
        super(message, null, false, false, errorCode, true);
        this.tps = tps;
        this.isServer = isServer;
    }

    public int getTps() {
        return tps;
    }

    public void setTps(int tps) {
        this.tps = tps;
    }

    public boolean isServer() {
        return isServer;
    }

    public void setServer(boolean server) {
        isServer = server;
    }
}
