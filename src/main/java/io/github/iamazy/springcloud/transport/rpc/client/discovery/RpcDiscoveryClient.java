package io.github.iamazy.springcloud.transport.rpc.client.discovery;


import io.github.iamazy.springcloud.transport.rpc.client.Client;
import io.github.iamazy.springcloud.transport.rpc.client.RpcClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author iamazy
 * @date 2018/12/21
 * @descrition
 **/
public class RpcDiscoveryClient implements DiscoveryClient{

    private Map<String, ? extends RpcClient> rpcClientMap;

    public void setRpcClientMap(Map<String, ? extends RpcClient> rpcClientMap) {
        this.rpcClientMap = rpcClientMap;
    }

    @Override
    public String description() {
        return "Rpc Discovery Client";
    }

    @Override
    public Client getInstance(String poolName) {
        return rpcClientMap.getOrDefault(poolName, null);
    }

    @Override
    public List<String> getPoolNames() {
        return new ArrayList<>(rpcClientMap.keySet());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
