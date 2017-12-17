package com.pan.rpc.sample.server;

import com.pan.rpc.common.bean.RpcRequest;
import com.pan.rpc.common.bean.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author panzheng
 * @ClassName:
 * @Description:
 * @date 2017/12/17
 */
public class RpcServerHandle extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerHandle.class);

    private final Map<String,Object> handleMap;

    public RpcServerHandle(Map<String,Object> handleMap) {
        this.handleMap = handleMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {

        RpcResponse response = new RpcResponse();
        response.setRequestId(response.getRequestId());
        try {
            Object result = handle(rpcRequest);
            response.setResult(result);
        } catch (Exception e) {
            LOGGER.error("handle result failure",e);
            response.setException(e);
        }

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private Object handle(RpcRequest request) throws Exception {
        String serviceName = request.getInterfaceName();
        String serviceVersion = request.getServiceVersion();
        if (!StringUtils.isEmpty(serviceVersion)) {
            serviceName += "-" + serviceVersion;
        }
        Object serviceBean = handleMap.get(serviceName);
        if (serviceBean == null) {
            throw new RuntimeException(String.format("can not find service bean by key: %s",serviceName));
        }

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName,parameterTypes);
        return serviceFastMethod.invoke(serviceBean,parameters);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) {
        LOGGER.error("server caught exeception",cause);
        ctx.close();
    }
}
