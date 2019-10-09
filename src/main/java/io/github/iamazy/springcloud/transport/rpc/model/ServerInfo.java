package io.github.iamazy.springcloud.transport.rpc.model;

import com.google.common.base.Splitter;
import com.google.common.collect.MapMaker;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * @author iamazy
 * @date 2018/12/8
 **/
@Getter
@ToString
public class ServerInfo {

    private static ConcurrentMap<String, ServerInfo> allServerInfo=new MapMaker().weakValues().makeMap();

    private static Splitter splitter=Splitter.on(':');
    private final String host;
    private final int port;

    private ServerInfo(String hostAndPort){
        List<String> split=splitter.splitToList(hostAndPort);
        assert split.size()==2;
        this.host=split.get(0);
        this.port=Integer.parseInt(split.get(1));
    }

    public static ServerInfo of(String host, int port){
        return allServerInfo.computeIfAbsent(host+":"+port, ServerInfo::new);
    }

    public static ServerInfo of(String hostAndPort){
        return allServerInfo.computeIfAbsent(hostAndPort, ServerInfo::new);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this,obj);
    }


}
