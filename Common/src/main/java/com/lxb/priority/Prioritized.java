package com.lxb.priority;

import static java.lang.Integer.compare;

import java.util.Comparator;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-03-22
 */
public interface Prioritized extends Comparable<Prioritized> {
    int NORMAL_PRIORITY = 0;
    int MIN_PRIORITY = Integer.MAX_VALUE;
    int MAX_PRIORITY = Integer.MIN_VALUE;

    Comparator<Object> COMPARATOR = (one, two) -> {
        boolean b1 = one instanceof Prioritized;
        boolean b2 = two instanceof Prioritized;
        if (b1 && !b2) {        // one is Prioritized, two is not
            return -1;
        } else if (b2 && !b1) { // two is Prioritized, one is not
            return 1;
        } else if (b1 && b2) {  //  one and two both are Prioritized
            return ((Prioritized) one).compareTo((Prioritized) two);
        } else {                // Try to use @Priority Comparator
            return PriorityComparator.INSTANCE.compare(one, two);
        }
    };


    default int getPriority() {
        return NORMAL_PRIORITY;
    }

    @Override
    default int compareTo(Prioritized that) {
        return compare(this.getPriority(), that.getPriority());
    }

}
