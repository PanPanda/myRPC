package com.pan.rpc.registry;

/**
 * @author panzheng
 * @ClassName:服务发现接口
 * @Description:
 * @date 2017/12/17
 */
public interface ServiceDiscovery {

    String discover(String serviceName);

}
