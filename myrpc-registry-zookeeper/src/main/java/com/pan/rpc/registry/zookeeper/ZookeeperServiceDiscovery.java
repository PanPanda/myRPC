package com.pan.rpc.registry.zookeeper;

import com.pan.rpc.registry.ServiceDiscovery;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author panzheng
 * @ClassName:
 * @Description:
 * @date 2017/12/17
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);

    private String zkAddress;

    public ZookeeperServiceDiscovery(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    @Override
    public String discover(String serviceName) {

        ZkClient zkClient = new ZkClient(zkAddress,Constant.ZK_SESSION_TIMEOUT,Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper");
        try {
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
            if (!zkClient.exists(servicePath)) {
                throw new RuntimeException(String.format("can not find any service node on path: %s",servicePath));
            }
            List<String> addressList = zkClient.getChildren(servicePath);
            if (null == addressList || addressList.size() == 0) {
                throw new RuntimeException(String.format("can not find any service node on path: %s",servicePath));
            }

            String address;
            int size = addressList.size();
            if (size == 1) {
                address = addressList.get(0);
                LOGGER.debug("get only address node: {}",address);
            } else {
                address = addressList.get(ThreadLocalRandom.current().nextInt(size));
                LOGGER.debug("get random address node: {}",address);
            }

            String addressPath = servicePath + "/" + address;
            return zkClient.readData(addressPath);
        } finally {
            zkClient.close();
        }
    }
}
