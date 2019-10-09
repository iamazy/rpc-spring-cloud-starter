package io.github.iamazy.springcloud.transport.rpc.thrift.impl;

import io.github.iamazy.springcloud.transport.rpc.strategy.FailoverCheckingStrategy;
import io.github.iamazy.springcloud.transport.rpc.thrift.utils.ThriftClientUtils;
import io.github.iamazy.springcloud.transport.rpc.model.ServerInfo;
import io.github.iamazy.springcloud.transport.rpc.thrift.ThriftClient;
import io.github.iamazy.springcloud.transport.rpc.thrift.pool.ThriftConnectionPoolProvider;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;

/**
 * @author iamazy
 * @date 2018/12/8
 **/
public class FailoverThriftClient implements ThriftClient {

    private final ThriftClient thriftClient;

    public FailoverThriftClient(
            FailoverCheckingStrategy<ServerInfo> failoverCheckingStrategy,
            Supplier<List<ServerInfo>> serverInfoProvider,
            ThriftConnectionPoolProvider poolProvider) {
        FailoverStrategy failoverStrategy=new FailoverStrategy(serverInfoProvider,poolProvider,failoverCheckingStrategy);
        this.thriftClient=new DefaultThriftClient(failoverStrategy,failoverStrategy);
    }

    @Override
    public <X extends TServiceClient> X iface(Class<X> clazz) {
        return thriftClient.iface(clazz);
    }

    @Override
    public <X extends TServiceClient> X iface(Class<X> clazz, int hash) {
        return thriftClient.iface(clazz,hash);
    }

    @Override
    public <X extends TServiceClient> X iface(Class<X> clazz, Function<TTransport, TProtocol> protocolProvider, int hash) {
        return thriftClient.iface(clazz,protocolProvider,hash);
    }

    @Override
    public <X extends TServiceClient> X mpiface(Class<X> clazz, String serviceName) {
        return thriftClient.mpiface(clazz,serviceName, ThriftClientUtils.nextInt());
    }

    @Override
    public <X extends TServiceClient> X mpiface(Class<X> clazz, String serviceName, int hash) {
        return thriftClient.mpiface(clazz,serviceName, TCompactProtocol::new,hash);
    }

    @Override
    public <X extends TServiceClient> X mpiface(Class<X> clazz, String serviceName, Function<TTransport, TProtocol> protocolProvider, int hash) {
        return thriftClient.mpiface(clazz,serviceName,protocolProvider,hash);
    }


    private class FailoverStrategy implements
            Supplier<List<ServerInfo>>,
            ThriftConnectionPoolProvider {

        private final Supplier<List<ServerInfo>> originalServerInfoProvider;

        private final ThriftConnectionPoolProvider connectionPoolProvider;

        private final FailoverCheckingStrategy<ServerInfo> failoverCheckingStrategy;

        private FailoverStrategy(Supplier<List<ServerInfo>> originalServerInfoProvider,
                                 ThriftConnectionPoolProvider connectionPoolProvider,
                                 FailoverCheckingStrategy<ServerInfo> failoverCheckingStrategy) {
            this.originalServerInfoProvider = originalServerInfoProvider;
            this.connectionPoolProvider = connectionPoolProvider;
            this.failoverCheckingStrategy = failoverCheckingStrategy;
        }

        @Override
        public List<ServerInfo> get() {
            Set<ServerInfo> failedServers = failoverCheckingStrategy.getFailed();
            return originalServerInfoProvider.get().stream()
                    .filter(i -> !failedServers.contains(i)).collect(toList());
        }

        @Override
        public TTransport getConnection(ServerInfo ServerInfo) {
            return connectionPoolProvider.getConnection(ServerInfo);
        }

        @Override
        public void returnConnection(ServerInfo ServerInfo, TTransport transport) {
            connectionPoolProvider.returnConnection(ServerInfo, transport);
        }

        @Override
        public void returnBrokenConnection(ServerInfo ServerInfo, TTransport transport) {
            failoverCheckingStrategy.fail(ServerInfo);
            connectionPoolProvider.returnBrokenConnection(ServerInfo, transport);
        }
    }
}
