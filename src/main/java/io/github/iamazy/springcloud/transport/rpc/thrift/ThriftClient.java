package io.github.iamazy.springcloud.transport.rpc.thrift;

import io.github.iamazy.springcloud.transport.rpc.client.RpcClient;
import io.github.iamazy.springcloud.transport.rpc.cons.RpcCategory;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import java.util.function.Function;

/**
 * @author iamazy
 * @date 2018/12/8
 **/

public interface ThriftClient extends RpcClient {

    /**
     * Rpc客户端的类型
     * @return THRIFT
     */
    @Override
    default RpcCategory category() {
        return RpcCategory.THRIFT;
    }

    <X extends TServiceClient> X iface(Class<X> clazz);

    <X extends TServiceClient> X iface(Class<X> clazz, int hash);

    <X extends TServiceClient> X iface(Class<X> clazz, Function<TTransport, TProtocol> protocolProvider, int hash);

    <X extends TServiceClient> X mpiface(Class<X> clazz, String serviceName);

    <X extends TServiceClient> X mpiface(Class<X> clazz, String serviceName, int hash);

    <X extends TServiceClient> X mpiface(Class<X> clazz, String serviceName, Function<TTransport, TProtocol> protocolProvider, int hash);
}
