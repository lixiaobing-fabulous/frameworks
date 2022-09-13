package hessian.io.java8;


import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;

/**
 *
 */
public class OffsetTimeHandle implements Java8TimeWrapper<OffsetTime> {

    private static final long serialVersionUID = -1701639933686935988L;
    protected LocalTime localTime;
    protected ZoneOffset zoneOffset;

    public OffsetTimeHandle() {
    }

    public OffsetTimeHandle(OffsetTime offsetTime) {
        wrap(offsetTime);
    }

    @Override
    public void wrap(final OffsetTime offsetTime) {
        this.zoneOffset = offsetTime.getOffset();
        this.localTime = offsetTime.toLocalTime();
    }

    @Override
    public OffsetTime readResolve() {
        return OffsetTime.of(localTime, zoneOffset);
    }
}
