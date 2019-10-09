package io.github.iamazy.springcloud.transport.rpc.thrift.utils;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * @author iamazy
 * @date 2018/12/8
 **/
public final class ThriftClientUtils {

    private static final Random RANDOM=new Random();

    private static ConcurrentMap<Class<?>, Set<String>> interfaceMethodCache=new ConcurrentHashMap<>();

    private ThriftClientUtils(){
        throw new UnsupportedOperationException();
    }

    public static int nextInt(){
        return RANDOM.nextInt();
    }

    public static Set<String> getInterfaceMethodNames(Class<?> clazz){
        return interfaceMethodCache.computeIfAbsent(clazz, i -> Stream.of(i.getInterfaces())
                .flatMap(c -> Stream.of(c.getMethods()))
                .map(Method::getName)
                .collect(toSet()));
    }
}
