package io.github.iamazy.springcloud.transport.rpc.client;


import io.github.iamazy.springcloud.transport.rpc.cons.RpcCategory;

/**
 * @author iamazy
 * @date 2018/12/20
 * @descrition
 **/
public interface RpcClient extends Client{

    default RpcCategory category() {
        return RpcCategory.THRIFT;
    }
}
