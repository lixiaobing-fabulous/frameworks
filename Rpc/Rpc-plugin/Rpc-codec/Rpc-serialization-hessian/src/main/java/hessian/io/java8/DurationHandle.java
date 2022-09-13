package hessian.io.java8;


import java.time.Duration;

/**
 * Duration包装器
 */
public class DurationHandle implements Java8TimeWrapper<Duration> {

    private static final long serialVersionUID = 8897104281275173430L;

    protected long seconds;
    protected int nano;

    public DurationHandle() {
    }

    public DurationHandle(Duration duration) {
        wrap(duration);
    }

    @Override
    public void wrap(final Duration duration) {
        this.seconds = duration.getSeconds();
        this.nano = duration.getNano();
    }

    @Override
    public Duration readResolve() {
        return Duration.ofSeconds(seconds, nano);
    }
}
