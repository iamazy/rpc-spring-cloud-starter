package io.github.iamazy.springcloud.transport.rpc.provider;


import io.github.iamazy.springcloud.transport.rpc.model.ServerInfo;

/**
 * @author iamazy
 * @date 2018/12/20
 * @descrition
 **/
public interface ConnectionPoolProvider<T> {


    /**
     * 获取连接池连接
     * @param rpcServerInfo
     * @return T
     */
    T getConnection(ServerInfo rpcServerInfo);


    /**
     * 返回连接池连接
     * @param rpcServerInfo
     * @param t
     */
    void returnConnection(ServerInfo rpcServerInfo, T t);

    /**
     * 返回中断连接池连接
     * @param rpcServerInfo
     * @param t
     */
    void returnBrokenConnection(ServerInfo rpcServerInfo, T t);
}
