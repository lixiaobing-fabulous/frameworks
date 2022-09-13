package hessian.io.java8;


import java.time.Year;
import java.time.temporal.ChronoField;

/**
 * Year包装器
 */
public class YearHandle implements Java8TimeWrapper<Year> {

    private static final long serialVersionUID = -2817915438221390743L;
    protected int year;

    public YearHandle() {
    }

    public YearHandle(Year year) {
        wrap(year);
    }

    @Override
    public void wrap(final Year time) {
        this.year = time.getValue();
    }

    @Override
    public Year readResolve() {
        ChronoField.YEAR.checkValidValue(year);
        return Year.of(year);
    }
}
