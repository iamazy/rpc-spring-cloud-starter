package io.github.iamazy.springcloud.transport.rpc.thrift.impl;

import io.github.iamazy.springcloud.transport.rpc.model.ServerInfo;
import io.github.iamazy.springcloud.transport.rpc.thrift.utils.ThriftClientUtils;
import io.github.iamazy.springcloud.transport.rpc.thrift.ThriftClient;
import io.github.iamazy.springcloud.transport.rpc.thrift.pool.ThriftConnectionPoolProvider;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author iamazy
 * @date 2018/12/8
 **/
@Slf4j
public class DefaultThriftClient implements ThriftClient {

    private final ThriftConnectionPoolProvider poolProvider;

    private final Supplier<List<ServerInfo>> serverInfoProvider;

    public DefaultThriftClient(Supplier<List<ServerInfo>> serverInfoProvider,
                               ThriftConnectionPoolProvider poolProvider){
        this.poolProvider=poolProvider;
        this.serverInfoProvider=serverInfoProvider;
    }

    @Override
    public void close() {
        poolProvider.close();
    }

    @Override
    public <X extends TServiceClient> X iface(Class<X> clazz) {
        return iface(clazz, ThriftClientUtils.nextInt());
    }

    @Override
    public <X extends TServiceClient> X iface(Class<X> clazz, int hash) {
        return iface(clazz, TCompactProtocol::new,hash);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X extends TServiceClient> X iface(Class<X> clazz, Function<TTransport, TProtocol> protocolProvider, int hash) {
        List<ServerInfo> serverInfos=serverInfoProvider.get();
        if(serverInfos==null||serverInfos.isEmpty()){
            throw new RuntimeException("Thrift服务列表不能为空!!!");
        }
        hash=Math.abs(hash);
        hash= Math.max(hash, 0);
        ServerInfo selected=serverInfos.get(hash%serverInfos.size());
        log.info("获取{}的thrift连接{},hash值为{},",clazz,selected,hash);
        TTransport transport=poolProvider.getConnection(selected);
        TProtocol protocol=protocolProvider.apply(transport);
        ProxyFactory factory=new ProxyFactory();
        factory.setSuperclass(clazz);
        factory.setFilter(method -> ThriftClientUtils.getInterfaceMethodNames(clazz).contains(method.getName()));
        try{
            X x =(X) factory.create(new Class[]{TProtocol.class},new Object[]{protocol});
            ((Proxy) x).setHandler((self,thisMethod,proceed,args)->{
                boolean success=false;
                try{
                    Object result=proceed.invoke(self,args);
                    success=true;
                    return result;
                }finally {
                    if(success){
                        poolProvider.returnConnection(selected,transport);
                    }else{
                        poolProvider.returnBrokenConnection(selected,transport);
                    }
                }
            });
            return x;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("创建Proxy失败!!!",e);
        }
    }

    @Override
    public <X extends TServiceClient> X mpiface(Class<X> clazz, String serviceName) {
        return mpiface(clazz,serviceName,ThriftClientUtils.nextInt());
    }

    @Override
    public <X extends TServiceClient> X mpiface(Class<X> clazz, String serviceName, int hash) {
        return mpiface(clazz,serviceName,TCompactProtocol::new,hash);
    }

    @Override
    public <X extends TServiceClient> X mpiface(Class<X> clazz, String serviceName, Function<TTransport, TProtocol> protocolProvider, int hash) {
        return iface(clazz,protocolProvider.andThen(p->new TMultiplexedProtocol(p,serviceName)),hash);
    }
}
