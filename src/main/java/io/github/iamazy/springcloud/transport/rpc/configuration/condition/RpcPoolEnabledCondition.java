package io.github.iamazy.springcloud.transport.rpc.configuration.condition;

import io.github.iamazy.springcloud.transport.rpc.configuration.RpcPoolConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author iamazy
 * @date 2019/1/12
 * @descrition
 **/
public class RpcPoolEnabledCondition extends SpringBootCondition {
    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        RpcPoolConfigurationProperties rpcPoolProperties=getRpcPoolProperties(context);
        if(!rpcPoolProperties.getEnabled()){
            return ConditionOutcome.noMatch("Rpc数据连接池功能被禁用,因为rpc.enabled=false!!!");
        }
        return ConditionOutcome.match();
    }

    private RpcPoolConfigurationProperties getRpcPoolProperties(ConditionContext context){
        RpcPoolConfigurationProperties rpcPoolConfigurationProperties=new RpcPoolConfigurationProperties(context.getEnvironment());
        Binder.get(context.getEnvironment()).bind("rpc", Bindable.ofInstance(rpcPoolConfigurationProperties));
        return rpcPoolConfigurationProperties;
    }
}
