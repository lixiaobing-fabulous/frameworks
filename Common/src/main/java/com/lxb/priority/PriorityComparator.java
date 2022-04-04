package com.lxb.priority;

import static com.lxb.utils.AnnotationUtils.findAnnotation;
import static com.lxb.utils.ClassUtil.resolveClass;

import java.util.Comparator;
import java.util.Objects;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-03-22
 */
public class PriorityComparator implements Comparator<Object> {
    private static final Class<Priority> PRIORITY_CLASS = Priority.class;
    private static final int UNDEFINED_VALUE = -1;

    public static final PriorityComparator INSTANCE = new PriorityComparator();

    @Override
    public int compare(Object o1, Object o2) {
        return compare(resolveClass(o1), resolveClass(o2));
    }

    public static int compare(Class<?> type1, Class<?> type2) {
        if (Objects.equals(type1, type2)) {
            return 0;
        }

        Priority priority1 = findAnnotation(type1, PRIORITY_CLASS);
        Priority priority2 = findAnnotation(type2, PRIORITY_CLASS);

        int priorityValue1 = getValue(priority1);
        int priorityValue2 = getValue(priority2);

        return Integer.compare(priorityValue1, priorityValue2);
    }



    private static int getValue(Priority priority) {
        int value = priority == null ? UNDEFINED_VALUE : priority.value();
        return value < 0 ? UNDEFINED_VALUE : value;
    }

}
