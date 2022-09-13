package hessian.io.java8;


import java.time.Period;

/**
 * Period包装器
 */
public class PeriodHandle implements Java8TimeWrapper<Period> {

    private static final long serialVersionUID = 3853269100626256850L;
    protected int years;
    protected int months;
    protected int days;

    public PeriodHandle() {
    }

    public PeriodHandle(Period period) {
        wrap(period);
    }

    @Override
    public void wrap(final Period period) {
        this.years = period.getYears();
        this.months = period.getMonths();
        this.days = period.getDays();
    }

    @Override
    public Period readResolve() {
        return Period.of(years, months, days);
    }
}
