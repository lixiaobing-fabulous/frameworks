package com.lxb.rpc.apm.health;


/**
 * 健康状态
 */
public enum HealthState {
    /**
     * Healthy health state.
     */
    HEALTHY((byte) 0, "healthy"),
    /**
     * Exhausted health state.
     */
    EXHAUSTED((byte) 1, "exhausted"),
    /**
     * Dead health state.
     */
    DEAD((byte) 2, "dead");

    private final byte status;
    private final String description;

    HealthState(byte status, String description) {
        this.status = status;
        this.description = description;
    }

    /**
     * Value of node heartbeat result . health state.
     *
     * @param b the b
     * @return the node heartbeat result . health state
     */
    public static HealthState valueOf(byte b) {
        switch (b) {
            case (byte) 0:
                return HEALTHY;
            case (byte) 1:
                return EXHAUSTED;
            default:
                return DEAD;
        }
    }
}
