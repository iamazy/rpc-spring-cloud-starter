package io.github.iamazy.springcloud.transport.rpc.configuration;

import io.github.iamazy.springcloud.transport.rpc.client.discovery.DiscoveryClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author iamazy
 * @date 2019/11/11
 * @descrition
 **/
@Slf4j
@Component
public class InitComponent implements DisposableBean {

    @Resource
    public DiscoveryClient discoveryClient;

    @Override
    public void destroy() {
        discoveryClient.getPoolNames().forEach(poolName->{
            discoveryClient.getInstance(poolName).close();
            log.info("数据连接池:["+poolName+"]关闭成功!!!");
        });
    }
}
