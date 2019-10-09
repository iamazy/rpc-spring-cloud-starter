package io.github.iamazy.springcloud.transport.rpc.client.discovery;

import io.github.iamazy.springcloud.transport.rpc.client.Client;
import org.springframework.core.Ordered;

import java.util.List;

/**
 * @author iamazy
 * @date 2018/12/21
 * @descrition
 **/
public interface DiscoveryClient extends Ordered {

    int DEFAULT_ORDER = 0;

    default String description(){return "寻找可用的Client";}

    Client getInstance(String poolName);

    List<String> getPoolNames();

    @Override
    default int getOrder() {
        return DEFAULT_ORDER;
    }

}
