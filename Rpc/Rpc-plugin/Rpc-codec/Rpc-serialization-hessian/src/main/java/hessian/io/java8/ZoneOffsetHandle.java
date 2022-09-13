package hessian.io.java8;


import java.time.ZoneOffset;

/**
 * ZoneOffset包装器
 */
public class ZoneOffsetHandle implements Java8TimeWrapper<ZoneOffset> {

    private static final long serialVersionUID = 867692662489392475L;
    private int seconds;

    public ZoneOffsetHandle() {
    }

    public ZoneOffsetHandle(ZoneOffset zoneOffset) {
        wrap(zoneOffset);
    }

    @Override
    public void wrap(final ZoneOffset zoneOffset) {
        this.seconds = zoneOffset.getTotalSeconds();
    }

    @Override
    public ZoneOffset readResolve() {
        return ZoneOffset.ofTotalSeconds(seconds);
    }
}
