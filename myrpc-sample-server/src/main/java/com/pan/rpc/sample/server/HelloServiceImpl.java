package com.pan.rpc.sample.server;

import com.pan.rpc.api.HelloService;
import com.pan.rpc.api.Person;

/**
 * @author panzheng
 * @ClassName:
 * @Description:
 * @date 2017/12/15
 */
public class HelloServiceImpl implements HelloService {


    @Override
    public String hello(String name) {
        return "Hello" + name;
    }

    @Override
    public String hello(Person person) {
        return "firstname is " + person.getFirstName() + "lastname is " + person.getLastName();
    }
}
