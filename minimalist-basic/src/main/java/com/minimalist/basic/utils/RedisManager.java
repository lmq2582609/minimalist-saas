package com.minimalist.basic.utils;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisManager {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 获取缓存
     * @param key 缓存key
     * @param <T> 泛型
     * @return 泛型
     */
    public <T> T get(String key) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    /**
     * 获取缓存 并删除
     * @param key 缓存key
     * @param <T> 泛型
     * @return 泛型
     */
    public <T> T getAndDelete(String key) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.getAndDelete();
    }

    /**
     * 删除缓存
     * @param key 缓存key
     */
    public void delete(String key) {
        redissonClient.getBucket(key).delete();
    }

    /**
     * 存放缓存
     * @param key 缓存key
     * @param value 缓存value
     * @param seconds 过期时间: 秒
     * @param <T> 泛型
     */
    public <T> void set(String key, T value, int seconds) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        bucket.set(value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 获取分布式锁
     * @param key 缓存key
     * @param waitTime 获取锁时等待的时间
     * @param leaseTime 持有锁的时间
     * @return true获取到锁，false未获取到
     */
    public boolean tryLock(String key, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(key);
        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("尝试获取分布式锁异常：", e);
        }
        return false;
    }

    /**
     * 释放锁
     * @param key 缓存key
     */
    public void unLock(String key) {
        RLock lock = redissonClient.getLock(key);
        //检查是否已加锁，已加锁返回true
        if (lock.isLocked()) {
            //检查是否是当前线程加的锁，是同一线程则释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 随机秒数
     * @return 秒数
     */
    public int randomSeconds() {
        return BigDecimal.valueOf(Math.random()).multiply(new BigDecimal(20)).intValue();
    }

}
