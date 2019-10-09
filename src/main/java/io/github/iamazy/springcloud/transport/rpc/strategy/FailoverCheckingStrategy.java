package io.github.iamazy.springcloud.transport.rpc.strategy;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.EvictingQueue;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;


/**
 * @author iamazy
 * @date 2018/12/8
 **/
@Slf4j
public class FailoverCheckingStrategy<T> {

    private static final int DEFAULT_FAIL_COUNT = 10;
    private static final long DEFAULT_FAIL_DURATION = MINUTES.toMillis(1);
    private static final long DEFAULT_RECOVERY_DURATION = MINUTES.toMillis(3);
    private final long failDuration;

    private final Cache<T, Boolean> failedList;

    private final LoadingCache<T, EvictingQueue<Long>> failCountMap;

    public FailoverCheckingStrategy(){
        this(DEFAULT_FAIL_COUNT,DEFAULT_FAIL_DURATION,DEFAULT_RECOVERY_DURATION);
    }


    public FailoverCheckingStrategy(int failCount, long failDuration, long recoveryDuration){
        this.failDuration=failDuration;
        this.failedList=newBuilder().weakKeys().expireAfterWrite(recoveryDuration,MILLISECONDS).build();
        this.failCountMap=newBuilder().weakKeys().build(new CacheLoader<T, EvictingQueue<Long>>() {
            @Override
            public EvictingQueue<Long> load(T t) {
                return EvictingQueue.create(failCount);
            }
        });
    }

    public Set<T> getFailed(){
        return failedList.asMap().keySet();
    }

    public void fail(T object){
        log.error("服务{}失败!!!",object);
        boolean addToFail=false;
        try {
            EvictingQueue<Long> evictingQueue=failCountMap.get(object);
            synchronized (evictingQueue){
                evictingQueue.add(System.currentTimeMillis());
                if(evictingQueue.remainingCapacity()==0&&
                    evictingQueue.element()>= System.currentTimeMillis()-failDuration){
                    addToFail=true;
                }
            }
        }catch (ExecutionException e){
            log.error("Ops.",e);
        }
        if(addToFail){
            failedList.put(object,Boolean.TRUE);
            log.error("服务{}失败!!!,添加到失败列表!!!",object);
        }
    }
}
