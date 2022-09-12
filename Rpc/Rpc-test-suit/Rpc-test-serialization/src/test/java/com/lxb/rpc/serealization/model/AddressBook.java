package com.lxb.rpc.serealization.model;


import java.util.ArrayList;
import java.util.List;

public class AddressBook {

    private List<Person> people;

    public AddressBook() {
    }

    public AddressBook(Person... people) {
        this.people = new ArrayList(people == null ? 0 : people.length);
        if (people != null) {
            for (Person person : people) {
                this.people.add(person);
            }
        }
    }

    public List<Person> getPeople() {
        return people;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AddressBook that = (AddressBook) o;

        return people != null ? people.equals(that.people) : that.people == null;
    }

    @Override
    public int hashCode() {
        return people != null ? people.hashCode() : 0;
    }
}
