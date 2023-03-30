package com.zerobase.cms.order.config;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

@TestConfiguration
public class TestRedisConfig {

    private final RedisServer redisServer;

    public TestRedisConfig(RedisProperties redisProperties){
        this.redisServer = RedisServer.builder().port(redisProperties.getPort()).build();
    }

    @PostConstruct
    public void startRedis(){
        redisServer.start();
    }
    @PreDestroy
    public void stopRedis(){
        redisServer.stop();
    }

}
