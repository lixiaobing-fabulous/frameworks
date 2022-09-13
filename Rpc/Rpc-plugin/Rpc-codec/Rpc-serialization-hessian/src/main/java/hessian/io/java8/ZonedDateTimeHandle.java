package hessian.io.java8;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * ZonedDateTime 包装器
 */
public class ZonedDateTimeHandle implements Java8TimeWrapper<ZonedDateTime> {

    private static final long serialVersionUID = -5652272593267656620L;

    protected LocalDateTime dateTime;
    protected ZoneOffset offset;
    protected String zoneId;

    public ZonedDateTimeHandle() {
    }

    public ZonedDateTimeHandle(ZonedDateTime zonedDateTime) {
        wrap(zonedDateTime);
    }

    @Override
    public void wrap(final ZonedDateTime zonedDateTime) {
        this.dateTime = zonedDateTime.toLocalDateTime();
        this.offset = zonedDateTime.getOffset();
        if (zonedDateTime.getZone() != null) {
            this.zoneId = zonedDateTime.getZone().getId();
        }
    }

    @Override
    public ZonedDateTime readResolve() {
        return ZonedDateTime.ofLocal(dateTime, ZoneId.of(zoneId), offset);
    }
}
