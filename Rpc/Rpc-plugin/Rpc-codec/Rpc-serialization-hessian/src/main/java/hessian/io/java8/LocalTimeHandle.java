package hessian.io.java8;



import java.time.LocalTime;

/**
 * LocalTime包装器
 */
public class LocalTimeHandle implements Java8TimeWrapper<LocalTime> {

    private static final long serialVersionUID = -4914516760659633510L;
    protected int hour;
    protected int minute;
    protected int second;
    protected int nano;

    public LocalTimeHandle() {
    }

    public LocalTimeHandle(LocalTime localTime) {
        wrap(localTime);
    }

    @Override
    public void wrap(final LocalTime localTime) {
        this.hour = localTime.getHour();
        this.minute = localTime.getMinute();
        this.second = localTime.getSecond();
        this.nano = localTime.getNano();

    }

    @Override
    public LocalTime readResolve() {
        return LocalTime.of(hour, minute, second, nano);
    }
}
