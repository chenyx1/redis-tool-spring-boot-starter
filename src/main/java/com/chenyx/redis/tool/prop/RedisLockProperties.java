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
}
