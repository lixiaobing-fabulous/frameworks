package com.lxb.rpc.serealization.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyBook {

    private List<Person> people;

    private Map<String, Employee> employees;

    public MyBook() {
    }

    public MyBook(Person... people) {
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

    public Map<String, Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Map<String, Employee> employees) {
        this.employees = employees;
    }

}
