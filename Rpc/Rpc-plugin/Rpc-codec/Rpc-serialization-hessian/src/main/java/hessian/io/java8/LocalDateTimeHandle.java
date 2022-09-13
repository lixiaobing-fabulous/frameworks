package hessian.io.java8;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * LocalDateTime包装器
 */
public class LocalDateTimeHandle implements Java8TimeWrapper<LocalDateTime> {

    private static final long serialVersionUID = 5535403026167970317L;
    protected LocalDate date;
    protected LocalTime time;

    public LocalDateTimeHandle() {
    }

    public LocalDateTimeHandle(LocalDateTime localDateTime) {
        wrap(localDateTime);
    }

    @Override
    public void wrap(final LocalDateTime localDateTime) {
        this.date = localDateTime.toLocalDate();
        this.time = localDateTime.toLocalTime();
    }

    @Override
    public LocalDateTime readResolve() {
        return LocalDateTime.of(date, time);
    }
}
