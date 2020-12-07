package com.chenyx.redis.tool.lock;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.redisson.RedissonLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
class RedissonLockTest {

    @Autowired
    private RedissonClient redisson;
    @Test
    void tryLock() throws Exception{
        RLock lock = redisson.getLock("test lock");
        lock.lock();
        System.out.print("result:{}" +  ",业务处理中。。。。。");
        lock.unlock();
    }

    @Test
    void unLock() {
    }

    @Test
    void testLock() {
    }
}