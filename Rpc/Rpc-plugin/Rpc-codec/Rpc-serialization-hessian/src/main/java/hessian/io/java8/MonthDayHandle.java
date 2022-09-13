package hessian.io.java8;


import java.time.MonthDay;

/**
 * MonthDay包装器
 */
public class MonthDayHandle implements Java8TimeWrapper<MonthDay> {
    private static final long serialVersionUID = 1842458305984530303L;
    protected int month;
    protected int day;

    public MonthDayHandle() {
    }

    public MonthDayHandle(MonthDay monthDay) {
        wrap(monthDay);
    }

    @Override
    public void wrap(final MonthDay monthDay) {
        this.month = monthDay.getMonthValue();
        this.day = monthDay.getDayOfMonth();
    }

    @Override
    public MonthDay readResolve() {
        return MonthDay.of(month, day);
    }
}
