package io.github.iamazy.springcloud.transport.rpc.thrift.pool;

import io.github.iamazy.springcloud.transport.rpc.model.ServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.transport.TTransport;

import java.util.function.Function;

/**
 * @author iamazy
 * @date 2018/12/8
 **/
@Slf4j
public final class ThriftConnectionFactory implements KeyedPooledObjectFactory<ServerInfo, TTransport> {

    private final Function<ServerInfo,TTransport> transportProvider;

    public ThriftConnectionFactory(Function<ServerInfo,TTransport> transportProvider){
        this.transportProvider=transportProvider;
    }

    @Override
    public PooledObject<TTransport> makeObject(ServerInfo serverInfo) throws Exception {
        TTransport transport=transportProvider.apply(serverInfo);
        transport.open();
        DefaultPooledObject<TTransport> result=new DefaultPooledObject<>(transport);
        log.info("创建了一个thrift连接:{}", serverInfo);
        return result;
    }

    @Override
    public void destroyObject(ServerInfo ServerInfo, PooledObject<TTransport> pooledObject) {
        TTransport transport=pooledObject.getObject();
        if(transport!=null&&transport.isOpen()){
            transport.close();
            log.info("关闭thrift连接:{}", ServerInfo);
        }
    }

    @Override
    public boolean validateObject(ServerInfo serverInfo, PooledObject<TTransport> pooledObject) {
        try {
            return pooledObject.getObject().isOpen();
        }catch (Throwable e){
            log.error("无法验证thrift Socket连接:{}", serverInfo,e);
            return false;
        }
    }

    @Override
    public void activateObject(ServerInfo serverInfo, PooledObject<TTransport> pooledObject) throws Exception {
        log.debug("激活thrift连接:"+pooledObject.getObject());
    }

    @Override
    public void passivateObject(ServerInfo serverInfo, PooledObject<TTransport> pooledObject) throws Exception {
        log.debug("钝化thrift连接:"+pooledObject.getObject());
    }
}
