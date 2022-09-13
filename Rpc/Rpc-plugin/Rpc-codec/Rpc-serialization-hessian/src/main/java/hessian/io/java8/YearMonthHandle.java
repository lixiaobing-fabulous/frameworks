package hessian.io.java8;


import java.time.YearMonth;

/**
 * YearMonth包装器
 */
public class YearMonthHandle implements Java8TimeWrapper<YearMonth> {

    private static final long serialVersionUID = 4927217299568145798L;
    protected int year;
    protected int month;

    public YearMonthHandle() {
    }

    public YearMonthHandle(YearMonth yearMonth) {
        wrap(yearMonth);
    }

    @Override
    public void wrap(final YearMonth yearMonth) {
        this.year = yearMonth.getYear();
        this.month = yearMonth.getMonthValue();
    }

    @Override
    public YearMonth readResolve() {
        return YearMonth.of(year, month);
    }
}
