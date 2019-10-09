package io.github.iamazy.springcloud.transport.rpc.thrift.pool;

import io.github.iamazy.springcloud.transport.rpc.provider.ConnectionPoolProvider;
import org.apache.thrift.transport.TTransport;

/**
 * @author iamazy
 * @date 2018/12/8
 **/
public interface ThriftConnectionPoolProvider extends ConnectionPoolProvider<TTransport> {
}
