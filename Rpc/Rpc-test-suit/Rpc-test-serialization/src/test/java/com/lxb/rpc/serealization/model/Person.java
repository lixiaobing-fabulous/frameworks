package com.lxb.rpc.serealization.model;


import java.util.ArrayList;
import java.util.List;

public class Person {

    private String name;
    private int id;
    private String email;
    private List<PhoneNumber> phones;

    public Person() {
    }

    public Person(String name, int id, String email, PhoneNumber... phones) {
        this.name = name;
        this.id = id;
        this.email = email;
        this.phones = new ArrayList<>(phones == null ? 0 : phones.length);
        if (phones != null) {
            for (PhoneNumber phone : phones) {
                this.phones.add(phone);
            }
        }
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<PhoneNumber> getPhones() {
        return phones;
    }

    public void setPhones(List<PhoneNumber> phones) {
        this.phones = phones;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Person person = (Person) o;

        if (id != person.id) {
            return false;
        }
        if (name != null ? !name.equals(person.name) : person.name != null) {
            return false;
        }
        if (email != null ? !email.equals(person.email) : person.email != null) {
            return false;
        }
        return phones != null ? phones.equals(person.phones) : person.phones == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + id;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phones != null ? phones.hashCode() : 0);
        return result;
    }
}
