package com.pan.rpc.api;

/**
 * @author panzheng
 * @ClassName: HelloService
 * @Description:
 * @date 2017/12/15
 */
public interface HelloService {

    String hello(String name);

    String hello(Person person);

}
