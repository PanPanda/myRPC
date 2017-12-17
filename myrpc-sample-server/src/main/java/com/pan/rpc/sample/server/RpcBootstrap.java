package com.pan.rpc.sample.server;

/**
 * @author panzheng
 * @ClassName:
 * @Description:
 * @date 2017/12/17
 */
public class RpcBootstrap {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring.xml");
    }

}
