package com.pan.rpc.common.bean;

/**
 * @author panzheng
 * @ClassName:
 * @Description:
 * @date 2017/12/17
 */
public class RpcResponse {

    private String requestId;
    private Exception exception;
    private Object result;

    public boolean hasExveption() {
        return exception != null;
    }

    public Throwable getException() {
        return exception;
    }

    public String getRequestId() {
        return requestId;
    }

    public RpcResponse setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public RpcResponse setException(Exception exception) {
        this.exception = exception;
        return this;
    }

    public Object getResult() {
        return result;
    }

    public RpcResponse setResult(Object result) {
        this.result = result;
        return this;
    }
}
