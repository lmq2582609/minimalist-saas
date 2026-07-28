package com.minimalist.basic.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
public class RedisHealthCheckTask {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 每5秒执行一次 Redis 状态检查
     * 单位：毫秒
     */
    @Scheduled(fixedRate = 5000)
    public void checkRedisStatus() {
        try {
            Boolean isAlive = redisTemplate.execute((RedisConnection connection) -> "PONG".equals(connection.ping()));
            if (Boolean.FALSE.equals(isAlive)) {
                log.info("Redis定时监测，ping失败");
            }
        } catch (Exception e) {
            log.error("Redis定时监测连接异常", e);
        }
    }

}
