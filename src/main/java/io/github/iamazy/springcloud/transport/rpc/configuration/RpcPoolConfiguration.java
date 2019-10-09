package io.github.iamazy.springcloud.transport.rpc.configuration;


import io.github.iamazy.springcloud.transport.rpc.client.RpcClient;
import io.github.iamazy.springcloud.transport.rpc.client.discovery.DiscoveryClient;
import io.github.iamazy.springcloud.transport.rpc.client.discovery.RpcDiscoveryClient;
import io.github.iamazy.springcloud.transport.rpc.configuration.condition.RpcPoolEnabledCondition;
import io.github.iamazy.springcloud.transport.rpc.model.ServerInfo;
import io.github.iamazy.springcloud.transport.rpc.model.rpc.RpcPool;
import io.github.iamazy.springcloud.transport.rpc.strategy.FailoverCheckingStrategy;
import io.github.iamazy.springcloud.transport.rpc.thrift.impl.FailoverThriftClient;
import io.github.iamazy.springcloud.transport.rpc.thrift.pool.DefaultThriftConnectionPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author iamazy
 * @date 2018/12/8
 **/
@Slf4j
@Configuration
@Conditional(RpcPoolEnabledCondition.class)
@EnableConfigurationProperties(RpcPoolConfigurationProperties.class)
public class RpcPoolConfiguration {

    @Resource
    RpcPoolConfigurationProperties rpcPoolProperties;

    @Bean(name = "rpcDiscoveryClient")
    public DiscoveryClient discoveryClient(){
        RpcDiscoveryClient discoveryClient= new RpcDiscoveryClient();
        discoveryClient.setRpcClientMap(rpcClientMap());
        return discoveryClient;
    }

    private Map<String, ? extends RpcClient> rpcClientMap(){

        FailoverCheckingStrategy<ServerInfo> failoverCheckingStrategy = new FailoverCheckingStrategy<>(
                10, TimeUnit.SECONDS.toMillis(30), TimeUnit.MINUTES.toMillis(1));

        Map<String,RpcClient> rpcClientMap=new HashMap<>(0);
        for(Map.Entry<String, RpcPool> entry: rpcPoolProperties.getPools().entrySet()){
            GenericKeyedObjectPoolConfig config=new GenericKeyedObjectPoolConfig();
            config.setMinIdlePerKey(entry.getValue().getMinIdlePerKey());
            config.setMaxIdlePerKey(entry.getValue().getMaxIdlePerKey());
            config.setMaxTotalPerKey(entry.getValue().getMaxTotalPerKey());
            config.setMaxTotal(entry.getValue().getMaxTotal());
            config.setLifo(entry.getValue().isLifo());
            config.setFairness(entry.getValue().isFairness());
            config.setMaxWaitMillis(entry.getValue().getMaxWaitMillis());
            config.setMinEvictableIdleTimeMillis(entry.getValue().getMinEvictableIdleTimeMillis());
            config.setSoftMinEvictableIdleTimeMillis(entry.getValue().getSoftMinEvictableIdleTimeMillis());
            config.setNumTestsPerEvictionRun(entry.getValue().getNumTestsPerEvictionRun());
            config.setEvictionPolicyClassName(entry.getValue().getEvictionPolicyClassName());
            config.setTestOnCreate(entry.getValue().isTestOnCreate());
            config.setTestOnBorrow(entry.getValue().isTestOnBorrow());
            config.setTestOnReturn(entry.getValue().isTestOnReturn());
            config.setTestWhileIdle(entry.getValue().isTestWhileIdle());
            config.setTimeBetweenEvictionRunsMillis(entry.getValue().getTimeBetweenEvictionRunsMillis());
            config.setBlockWhenExhausted(entry.getValue().isBlockWhenExhausted());
            config.setJmxEnabled(entry.getValue().isJmxEnabled());
            config.setJmxNamePrefix(entry.getValue().getJmxNamePrefix());
            config.setJmxNameBase(entry.getValue().getJmxNameBase());

            switch (entry.getValue().getCategory()){
                case THRIFT:{
                    Function<ServerInfo, TTransport> transportProvider= thriftServerInfo -> {
                        TSocket socket=new TSocket(thriftServerInfo.getHost(), thriftServerInfo.getPort());
                        return new TFramedTransport(socket,Integer.MAX_VALUE);
                    };
                    List<ServerInfo> serverInfoList=new ArrayList<>(0);
                    new HashSet<>(entry.getValue().getServerHostAndPorts()).forEach(hostAndPort-> serverInfoList.add(ServerInfo.of(hostAndPort)));

                    log.info("thrift连接池:"+entry.getValue()+"初始化完成...");
                    FailoverThriftClient thriftClient = new FailoverThriftClient(failoverCheckingStrategy,
                            () -> serverInfoList, new DefaultThriftConnectionPool(config, transportProvider));
                    rpcClientMap.put(entry.getKey(),thriftClient);
                }
                break;
                case PROTOBUF:{
                    break;
                }
                default:{
                    break;
                }
            }

        }
        return rpcClientMap;
    }

}
