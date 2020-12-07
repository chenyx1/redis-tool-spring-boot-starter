package com.chenyx.redis.tool.lock;

import com.chenyx.redis.tool.constant.RedisResultEnum;
import com.chenyx.redis.tool.prop.RedisLockProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @desc 利用lua函数实现redis分布式锁
 * @auhtor chenyx
 * @date 2020-11-09
 */
@Service
public class RedisLock extends BaseRedisLock {

    @Autowired
    private RedisLockProperties redisLockProperties;
    @Resource
    protected StringRedisTemplate stringRedisTemplate;


    @Override
    @Deprecated
    public void lock() {
        try {
            int wiatTime = 1;//没有获取锁等待时间
            int count = 0;//等待次数
            String synKey = redisLockProperties.getSynKey() + "#" + Thread.currentThread().getId();
            String synValue = redisLockProperties.getSynValue() + "#" + Thread.currentThread().getId();
            while (!tryLock(synKey,synValue,
                    redisLockProperties.getExpireTime())) {
                int sleepTime = (wiatTime << count) * 1000;
                Thread.sleep(sleepTime);
                count ++;
            }
        }catch (InterruptedException e) {
           throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    @Deprecated
    public void unlock() {
        try {
            int wiatTime = 1;//没有获取锁等待时间
            int count = 0;//等待次数
            String synKey = redisLockProperties.getSynKey() + "#" + Thread.currentThread().getId();
            String synValue = redisLockProperties.getSynValue() + "#" + Thread.currentThread().getId();
            while (!unLock(synKey,synValue)) {
                int sleepTime = (wiatTime << count) * 1000;
                Thread.sleep(sleepTime);
                count ++;
            }
        }catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public boolean tryLock(String key, String value, Long expireTime)throws RuntimeException {
        boolean ret = false;
        try {
            if (expireTime == null) {
                expireTime = redisLockProperties.getExpireTime();
            }
            expireTime  = expireTime + redisLockProperties.getTimeOut();
            String script = "if redis.call('setNx',KEYS[1],ARGV[1]) == 1 then if redis.call('get',KEYS[1])==ARGV[1] then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end else return 0 end";
            RedisScript<Object> redisScript = new DefaultRedisScript<>(script, Object.class);
            Object result = stringRedisTemplate.execute(redisScript, Collections.singletonList(key),value,expireTime);
            if (result == null) {
                return ret;
            }
            if (result instanceof Collection) {
                if (RedisResultEnum.SUC.getCode().equals(Integer.valueOf(((List) result).get(0) + ""))) {
                    ret = true;
                }
            } else {
                if (RedisResultEnum.SUC.getCode().equals(Integer.valueOf(result + ""))) {
                    ret = true;
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return ret;
    }

    @Override
    public boolean unLock(String key, String value)throws RuntimeException {
        boolean ret = false;
        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            RedisScript<Object> redisScript = new DefaultRedisScript<>(script, Object.class);
            Object result = stringRedisTemplate.execute(redisScript, Collections.singletonList(key),value);
            if (result == null) {
                return ret;
            }
            if (result instanceof Collection) {
                if (RedisResultEnum.SUC.getCode().equals(Integer.valueOf(((List) result).get(0) + ""))) {
                    ret = true;
                }
            } else {
                if (RedisResultEnum.SUC.getCode().equals(Integer.valueOf(result + ""))) {
                    ret = true;
                }
            }
        } catch (RuntimeException e) {
           throw e;
        }
        return ret;
    }
}
