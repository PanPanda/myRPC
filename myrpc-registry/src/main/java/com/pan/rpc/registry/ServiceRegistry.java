package com.pan.rpc.registry;

/**
 * @author panzheng
 * @ClassName:
 * @Description:服务器注册接口
 * @date 2017/12/17
 */
public interface ServiceRegistry {

    void register(String serviceName,String serviceAddress);

}
