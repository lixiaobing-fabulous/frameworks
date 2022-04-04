package com.lxb.utils;

import static com.lxb.function.Streams.filter;
import static com.lxb.function.Streams.filterFirst;
import static com.lxb.utils.ClassUtil.getAllInheritedTypes;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author lixiaobing <lixiaobing@kuaishou.com>
 * Created on 2022-03-22
 */
public class AnnotationUtils {
    public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
        return findAnnotation(annotatedElement, a -> isSameType(a, annotationType));
    }

    public static <A extends Annotation> A findAnnotation(AnnotatedElement annotatedElement,
                                                          Predicate<Annotation>... annotationFilters) {
        return (A) filterFirst(getAllDeclaredAnnotations(annotatedElement), annotationFilters);
    }

    public static List<Annotation> getAllDeclaredAnnotations(AnnotatedElement annotatedElement,
                                                             Predicate<Annotation>... annotationsToFilter) {
        if (isClass(annotatedElement)) {
            return getAllDeclaredAnnotations((Class) annotatedElement, annotationsToFilter);
        } else {
            return getDeclaredAnnotations(annotatedElement, annotationsToFilter);
        }
    }

    public static List<Annotation> getAllDeclaredAnnotations(Class<?> type,
                                                             Predicate<Annotation>... annotationsToFilter) {
        if (type == null) {
            return emptyList();
        }
        List<Annotation> allAnnotations = new LinkedList<>();

        // All types
        Set<Class<?>> allTypes = new LinkedHashSet<>();
        allTypes.add(type);
        allTypes.addAll(getAllInheritedTypes(type, t -> !Object.class.equals(t)));
        for (Class<?> t : allTypes) {
            allAnnotations.addAll(getDeclaredAnnotations(t));
        }

        return filter(allAnnotations, annotationsToFilter);

    }

    public static List<Annotation> getDeclaredAnnotations(AnnotatedElement annotatedElement,
                                                          Predicate<Annotation>... annotationsToFilter) {
        if (annotatedElement == null) {
            return emptyList();
        }

        return filter(asList(annotatedElement.getAnnotations()), annotationsToFilter);
    }


    public static boolean isClass(AnnotatedElement annotatedElement) {
        return annotatedElement instanceof Class;
    }


    public static boolean isSameType(Annotation annotation, Class<? extends Annotation> annotationType) {
        if (Objects.isNull(annotation) && Objects.isNull(annotationType)) {
            return false;
        }
        return Objects.equals(annotation.annotationType(), annotationType);
    }
}
