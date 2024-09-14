package com.minimalist.common.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RedissonConfiguration {

    @Autowired
    private Environment environment;

    /**
     * redisson单节点
     * @return RedissonClient
     */
    @Bean
    public RedissonClient getSingleRedissonClient() {
        Config config = new Config();
        String address = environment.getProperty("redisson.address");
        String password = environment.getProperty("redisson.password");
        Integer pingInterval = environment.getProperty("redisson.pingInterval", Integer.class);
        Integer connectionPoolSize = environment.getProperty("redisson.connectionPoolSize", Integer.class);
        Integer connectionMinimumIdleSize = environment.getProperty("redisson.connectionMinimumIdleSize", Integer.class);
        Integer database = environment.getProperty("redisson.database", Integer.class);
        config.useSingleServer()
                .setAddress(address)
                .setPassword(password)
                .setKeepAlive(true)
                .setDatabase(database)
                .setConnectionPoolSize(connectionPoolSize)
                .setConnectionMinimumIdleSize(connectionMinimumIdleSize)
                .setPingConnectionInterval(pingInterval);
        //设置redisson序列化与反序列化，jackson增加java时间模块处理
        JsonJacksonCodec jsonJacksonCodec = new JsonJacksonCodec();
        ObjectMapper objectMapper = jsonJacksonCodec.getObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        config.setCodec(jsonJacksonCodec);
        return Redisson.create(config);
    }

}
