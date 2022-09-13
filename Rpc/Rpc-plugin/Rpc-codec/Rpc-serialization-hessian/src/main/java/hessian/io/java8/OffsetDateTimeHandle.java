package hessian.io.java8;


import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * OffsetDateTime包装器
 */
public class OffsetDateTimeHandle implements Java8TimeWrapper<OffsetDateTime> {
    private static final long serialVersionUID = -4980501582624017719L;

    protected LocalDateTime dateTime;
    protected ZoneOffset offset;

    public OffsetDateTimeHandle() {
    }

    public OffsetDateTimeHandle(OffsetDateTime offsetDateTime) {
        wrap(offsetDateTime);
    }

    @Override
    public void wrap(final OffsetDateTime time) {
        this.dateTime = time.toLocalDateTime();
        this.offset = time.getOffset();
    }

    @Override
    public OffsetDateTime readResolve() {
        return OffsetDateTime.of(dateTime, offset);
    }
}
