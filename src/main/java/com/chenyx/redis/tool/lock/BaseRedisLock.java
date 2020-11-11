package com.chenyx.redis.tool.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
/**
 * @desc redis 分布式锁抽象类，用于提供基础的分布式锁的基础方法
 * @auhtor chenyx
 * @date 2020-11-09
 *
 * */
public abstract class BaseRedisLock implements Lock {
    @Override
    public void lock() {

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    /**
     * @desc 获取分布式锁
     * @auhtor chenyx
     * @date 2020-11-09
     * */
    public boolean tryLock(String key, String value,Long expireTime)throws InterruptedException {
        return false;
    }

    /**
     * @desc 释放分布式锁
     * @auhtor chenyx
     * @date 2020-11-09
     * */
    public boolean unLock(String key, String value)throws InterruptedException {
        return false;
    }
    @Override
    public void unlock() {

    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
