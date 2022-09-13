package hessian.io.java8;


import java.time.LocalDate;

/**
 * LocalDate包装器
 */
public class LocalDateHandle implements Java8TimeWrapper<LocalDate> {

    private static final long serialVersionUID = 8739388063645761985L;
    protected int year;
    protected int month;
    protected int day;

    public LocalDateHandle() {
    }

    public LocalDateHandle(LocalDate localDate) {
        wrap(localDate);
    }

    @Override
    public void wrap(final LocalDate localDate) {
        this.year = localDate.getYear();
        this.month = localDate.getMonthValue();
        this.day = localDate.getDayOfMonth();
    }

    @Override
    public LocalDate readResolve() {
        return LocalDate.of(year, month, day);
    }
}
