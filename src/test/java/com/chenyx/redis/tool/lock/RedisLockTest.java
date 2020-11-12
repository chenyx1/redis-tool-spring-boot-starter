package com.chenyx.redis.tool.lock;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
class RedisLockTest {

    @Autowired
    private RedisLock redisLock;
    @Test
    void tryLock() throws Exception{
        boolean result = redisLock.tryLock("key1","2",60L);
        System.out.print("result:{}" + result);

        if (!result) {
            System.out.print("result:{}" + result + ",m没有获取到锁");
            return;
        }
        System.out.print("result:{}" + result + ",业务处理中。。。。。");
       // redisLock.unLock("key1","2");
    }

    @Test
    void unLock() {
    }

    @Test
    void testLock() {
        redisLock.lock();
        redisLock.lock();
    }
}