package com.lxb.rpc.serealization;


import com.lxb.rpc.serealization.exception.NotFoundException;
import com.lxb.rpc.serealization.model.Animal;
import com.lxb.rpc.serealization.model.Employee;
import com.lxb.rpc.serealization.model.MyBook;

import java.util.concurrent.CompletableFuture;

public interface HelloWold {

    CompletableFuture<Integer> update(MyBook book) throws NotFoundException;

    void hello(AnimalTest<Employee> test);

    interface AnimalTest<T> {

        void hello(Animal<T> animal);
    }
}
