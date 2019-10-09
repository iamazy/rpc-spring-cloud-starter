# RPC 数据连接池 v1

#### 声明
这是之前看到的别人用common-pool2实现的thrift连接池，但是找不到作者(好像是个歪果仁)的github地址了，有人知道的请告诉我一下蟹蟹

#### 1.配置application.yml
```yaml
rpc:
  enabled: true
  pools: 
    auth:
      category: thrift
      max-total: 1000
      max-idle-per-key: 1000
      max-total-per-key: 1000
      min-idle-per-key: 0
      test-on-borrow: false
      min-evictable-idle-time-millis: 3000000
      soft-min-evictable-idle-time-millis: 3000000
      jmx-enabled: false
      server-host-and-ports:
        -  192.168.12.166:8989
        -  192.168.2.130:8989
    retrieve-cities:
      #枚举类型  小写也可以
      category: thrift
      max-total: 500
      max-idle-per-key: 500
      max-total-per-key: 500
      min-idle-per-key: 0
      test-on-borrow: false
      min-evictable-idle-time-millis: 1500000
      soft-min-evictable-idle-time-millis: 1500000
      jmx-enabled: false
      server-host-and-ports:
        -  192.168.2.135:9000  
```

#### 3.调用RPC客户端
```java
@Resource(name = "rpcDiscoveryClient")
 private DiscoveryClient discoveryClient;
ThriftClient thriftClient=(ThriftClient) discoveryClient.getInstance("auth");
thriftClient.mpiface(TUserService.Client.class, RpcService.USER_SERVICE.getName()).get(username);
```