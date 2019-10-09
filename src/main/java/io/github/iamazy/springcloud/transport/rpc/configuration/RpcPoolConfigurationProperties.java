package io.github.iamazy.springcloud.transport.rpc.configuration;

import io.github.iamazy.springcloud.transport.rpc.model.rpc.RpcPool;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author iamazy
 * @date 2018/12/8
 **/
@Data
@ConfigurationProperties(prefix = "rpc",ignoreUnknownFields = false)
public class RpcPoolConfigurationProperties {

    private Boolean enabled=false;

    private Map<String, RpcPool> pools=new LinkedHashMap<>(0);

    private final Environment environment;

    public RpcPoolConfigurationProperties(Environment environment){
        Assert.notNull(environment, "Environment must not be null");
        this.environment = environment;
    }

}
