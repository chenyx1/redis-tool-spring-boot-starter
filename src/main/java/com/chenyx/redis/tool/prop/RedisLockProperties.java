package com.chenyx.redis.tool.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @desc 分布式锁属性
 * @auhtor chenyx
 * @date 2020-11-10
 * */
@Component
@ConfigurationProperties(prefix = "spring.redis.lock")
public class RedisLockProperties {

    //过期时间,单位以秒计
    private Long expireTime =1 * 60L;

    //网络超时,单位以秒计
    private Long timeOut = 1 * 60L;

    //同步锁对应的key
    private String synKey = "synKey";

    //同步锁对应的Value
    private String synValue = "synValue";


    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public Long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Long timeOut) {
        this.timeOut = timeOut;
    }

    public String getSynKey() {
        return synKey;
    }

    public void setSynKey(String synKey) {
        this.synKey = synKey;
    }

    public String getSynValue() {
        return synValue;
    }

    public void setSynValue(String synValue) {
        this.synValue = synValue;
    }
}
