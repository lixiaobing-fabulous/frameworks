package hessian.io.java8;


import java.time.ZoneId;

/**
 * ZoneId包装器
 */
public class ZoneIdHandle implements Java8TimeWrapper<ZoneId> {

    private static final long serialVersionUID = -2920446760568797907L;
    protected String zoneId;

    public ZoneIdHandle() {
    }

    public ZoneIdHandle(ZoneId zoneId) {
        wrap(zoneId);
    }

    @Override
    public void wrap(ZoneId zoneId) {
        this.zoneId = zoneId.getId();
    }

    @Override
    public ZoneId readResolve() {
        return ZoneId.of(zoneId);
    }
}
