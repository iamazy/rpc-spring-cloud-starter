package io.github.iamazy.springcloud.transport.rpc.thrift.pool;

import io.github.iamazy.springcloud.transport.rpc.model.ServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.util.function.Function;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * @author iamazy
 * @date 2018/12/8
 **/
@Slf4j
public class DefaultThriftConnectionPool implements ThriftConnectionPoolProvider{

    private static final int TIMEOUT = (int) MINUTES.toMillis(5);

    private final GenericKeyedObjectPool<ServerInfo,TTransport> connections;

    public DefaultThriftConnectionPool(GenericKeyedObjectPoolConfig<TTransport> config,
                                       Function<ServerInfo,TTransport> transportProvider){
        connections=new GenericKeyedObjectPool<>(new ThriftConnectionFactory(transportProvider),config);
    }

    public DefaultThriftConnectionPool(GenericKeyedObjectPoolConfig<TTransport> config){
        this(config,thriftServerInfo -> {
            TSocket socket=new TSocket(thriftServerInfo.getHost(),thriftServerInfo.getPort());
            socket.setTimeout(TIMEOUT);
            return new TFramedTransport(socket);
        });
    }

    @Override
    public TTransport getConnection(ServerInfo serverInfo) {
        try {
            return connections.borrowObject(serverInfo);
        }catch (Exception e){
            log.error("获取thrift连接失败,info:"+ serverInfo,e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void returnConnection(ServerInfo serverInfo, TTransport transport) {
        connections.returnObject(serverInfo,transport);
    }

    @Override
    public void returnBrokenConnection(ServerInfo serverInfo, TTransport transport) {
        try {
            connections.invalidateObject(serverInfo,transport);
        }catch (Exception e){
            log.error("无法验证该thrift连接:{},{}", serverInfo,transport,e);
        }
    }

    @Override
    public void close(){
        connections.close();
    }



}
