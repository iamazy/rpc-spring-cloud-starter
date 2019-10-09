package io.github.iamazy.springcloud.transport.rpc.model.rpc;

import io.github.iamazy.springcloud.transport.rpc.cons.RpcCategory;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author iamazy
 * @date 2018/12/21
 * @descrition
 **/
@Data
public class RpcPool {

    private RpcCategory category;
    /**
     * 连接池ip和port列表
     */
    private List<String> serverHostAndPorts=new ArrayList<>(0);

    /**
     * 最小空闲连接数per key
     */
    private int minIdlePerKey = 0;
    /**
     * 最大空闲连接数per key
     */
    private int maxIdlePerKey = 8;
    /**
     * 最大总连接数per key
     */
    private int maxTotalPerKey = 8;
    /**
     * 最大总连接数
     */
    private int maxTotal = -1;
    /**
     * 资源的存取数据结构,true:资源按照栈结构存取，false:资源按照队列结构存取
     */
    private boolean lifo = true;
    /**
     * 从池中获取资源或者将资源还回池中时,是否使用公平锁机制
     */
    private boolean fairness = false;
    /**
     * 获取资源的最大等待时间，-1代表时间无限制
     */
    private long maxWaitMillis = -1L;
    /**
     * 间隔多久进行一次检测,检测需要关闭的空闲连接
     */
    private long minEvictableIdleTimeMillis = 1800000L;
    /**
     * 软资源最小空闲时间
     */
    private long softMinEvictableIdleTimeMillis = 1800000L;
    /**
     *  资源回收线程执行一次回收操作,回收资源的数量. 默认值 3
     */
    private int numTestsPerEvictionRun = 3;
    /**
     * 资源回收策略
     */
    private String evictionPolicyClassName = "org.apache.commons.pool2.impl.DefaultEvictionPolicy";
    /**
     * 创建连接时验证是否有效
     */
    private boolean testOnCreate = false;

    /**
     * 获取连接池里的数据库连接时判断是否可用,生产环境一般不开启,会有性能问题
     */
    private boolean testOnBorrow = false;
    /**
     * 生产环境一般不开启,会有性能问题
     */
    private boolean testOnReturn = false;
    /**
     * 判断空闲连接是否失效
     */
    private boolean testWhileIdle = false;
    /**
     * 回收资源线程的执行周期,单位毫秒. -1 表示不启用线程回收资源
     */
    private long timeBetweenEvictionRunsMillis = -1L;
    /**
     * 当资源耗尽时,是否阻塞等待获取资源
     */
    private boolean blockWhenExhausted = true;
    /**
     * 是否启用jmx监控
     */
    private boolean jmxEnabled = true;
    /**
     * jmx命名前缀
     */
    private String jmxNamePrefix = "pool";
    /**
     * jmx命名
     */
    private String jmxNameBase= StringUtils.EMPTY;


}