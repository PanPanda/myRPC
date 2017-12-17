package com.pan.rpc.sample.server;

import com.pan.rpc.common.bean.RpcRequest;
import com.pan.rpc.common.bean.RpcResponse;
import com.pan.rpc.common.codec.RpcDecoder;
import com.pan.rpc.common.codec.RpcEncoder;
import com.pan.rpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author panzheng
 * @ClassName:
 * @Description:
 * @date 2017/12/17
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private String serviceAddress;

    private ServiceRegistry serviceRegistry;

    private Map<String, Object> handlerMap = new HashMap<>();

    public RpcServer(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public RpcServer(String serviceAddress, ServiceRegistry serviceRegistry) {
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup);
            bootstrap.channel(NioSctpServerChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline()
                            .addLast(new RpcDecoder(RpcRequest.class))
                            .addLast(new RpcEncoder(RpcResponse.class))
                            .addLast(new RpcServerHandle(handlerMap));
                }
            });

            bootstrap.option(ChannelOption.SO_BACKLOG,1024);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);

            String[] addressArray = StringUtils.split(serviceAddress,":");
            String ip = addressArray[0];
            int port = Integer.parseInt(addressArray[1]);

            ChannelFuture future = bootstrap.bind(ip,port).sync();

            if (serviceRegistry != null) {
                handlerMap.keySet().forEach(d->{
                    serviceRegistry.register(d,serviceAddress);
                    LOGGER.debug("register service: {}=> {}",d,serviceAddress);
                });
            }
            LOGGER.debug("server started on port {}",port);

            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {

        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            serviceBeanMap.values().forEach(bean -> {
                        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
                        String serviceName = rpcService.value().getName();
                        String serviceVersion = rpcService.version();
                        if (null != serviceVersion && !"".equals(serviceName)) {
                            serviceName += "-" + serviceVersion;
                        }
                        handlerMap.put(serviceName,bean);
                    }
            );
        }
    }
}
