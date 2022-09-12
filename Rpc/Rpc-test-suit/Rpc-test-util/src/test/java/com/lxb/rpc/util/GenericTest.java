package com.lxb.rpc.util;


import com.lxb.rpc.exception.MethodOverloadException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;

public class GenericTest {

    @Test
    public void testGeneric() throws NoSuchMethodException, MethodOverloadException {
        GenericClass genericClass = ClassUtils.getGenericClass(AppleService.class);
        Method method = ClassUtils.getPublicMethod(AppleService.class, "getPrice");
        GenericMethod genericMethod = genericClass.get(method);
        GenericType[] genericTypes = genericMethod.getParameters();
        Assertions.assertEquals(genericTypes.length, 1);
        Assertions.assertEquals(genericTypes[0].getGenericType(), Apple.class);
        method = ClassUtils.getPublicMethod(AppleService.class, "add2ShopCar");
        genericMethod = genericClass.get(method);
        genericTypes = genericMethod.getParameters();
        Assertions.assertEquals(genericTypes.length, 1);
        Assertions.assertTrue(genericTypes[0].getGenericType() instanceof GenericArrayType);
        Assertions.assertEquals(((GenericArrayType) genericTypes[0].getGenericType()).getGenericComponentType(), Apple.class);
        method = ClassUtils.getPublicMethod(AppleService.class, "delete");
        genericMethod = genericClass.get(method);
        genericTypes = genericMethod.getParameters();
        Assertions.assertEquals(genericTypes.length, 1);
        Assertions.assertTrue(genericTypes[0].getGenericType() instanceof ParameterizedType);
        Assertions.assertEquals(((ParameterizedType) genericTypes[0].getGenericType()).getActualTypeArguments()[0], Apple.class);

        method = ClassUtils.getPublicMethod(AppleService.class, "update");
        genericMethod = genericClass.get(method);
        genericTypes = genericMethod.getParameters();
        Assertions.assertEquals(genericTypes.length, 1);
        Assertions.assertTrue(genericTypes[0].getGenericType() instanceof TypeVariable);
        GenericType.Variable variable = genericTypes[0].getVariable(((TypeVariable) genericTypes[0].getGenericType()).getName());
        Assertions.assertTrue(variable.getGenericType() instanceof TypeVariable);
        Type bound = ((TypeVariable) variable.getGenericType()).getBounds()[0];
        Assertions.assertTrue(bound instanceof ParameterizedType);
        Assertions.assertEquals(((ParameterizedType) bound).getActualTypeArguments()[0], Apple.class);

        method = ClassUtils.getPublicMethod(AppleService.class, "wildcard");
        genericMethod = genericClass.get(method);
        genericTypes = genericMethod.getParameters();
        Assertions.assertEquals(genericTypes.length, 1);
        Assertions.assertTrue(genericTypes[0].getGenericType() instanceof ParameterizedType);
        Assertions.assertTrue(((ParameterizedType) genericTypes[0].getGenericType()).getActualTypeArguments()[0] instanceof WildcardType);
        Assertions.assertEquals(((WildcardType) ((ParameterizedType) genericTypes[0].getGenericType()).getActualTypeArguments()[0]).getUpperBounds()[0], Apple.class);
    }

    public static class Fruit {

    }

    public static class Apple extends Fruit {

        protected int weight;

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }

    public interface FruitService<T extends Fruit> {

        double getPrice(T fruit);

        void add2ShopCar(T[] fruits);

        void delete(List<T> fruits);

        <B extends List<T>> void update(B fruits);

        void wildcard(List<? extends T> fruits);

    }

    public interface AppleService extends FruitService<Apple> {

    }
}
