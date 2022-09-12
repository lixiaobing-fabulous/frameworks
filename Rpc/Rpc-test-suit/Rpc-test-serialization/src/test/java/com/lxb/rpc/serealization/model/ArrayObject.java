package com.lxb.rpc.serealization.model;


import java.util.Arrays;

public class ArrayObject {

    public String[] strArray;
    public Foo[] fooArray;
    public Object[] objArray;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ArrayObject that = (ArrayObject) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(strArray, that.strArray)) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(fooArray, that.fooArray)) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(objArray, that.objArray);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(strArray);
        result = 31 * result + Arrays.hashCode(fooArray);
        result = 31 * result + Arrays.hashCode(objArray);
        return result;
    }

    public static final class Foo {
        protected String name;
        protected int id;

        public Foo() {
        }

        public Foo(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Foo foo = (Foo) o;

            if (id != foo.id) {
                return false;
            }
            return name != null ? name.equals(foo.name) : foo.name == null;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + id;
            return result;
        }
    }
}
