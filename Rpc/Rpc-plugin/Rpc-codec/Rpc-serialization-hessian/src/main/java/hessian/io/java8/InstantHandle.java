package hessian.io.java8;


import java.time.Instant;

/**
 * Instant包装器
 */
public class InstantHandle implements Java8TimeWrapper<Instant> {

    private static final long serialVersionUID = -1023781102104772052L;
    protected long seconds;
    protected int nano;

    public InstantHandle() {
    }

    public InstantHandle(Instant instant) {
        wrap(instant);
    }

    @Override
    public void wrap(final Instant instant) {
        if (instant == null) {
            return;
        }
        this.seconds = instant.getEpochSecond();
        this.nano = instant.getNano();
    }

    @Override
    public Instant readResolve() {
        return Instant.ofEpochSecond(seconds, nano);
    }
}
