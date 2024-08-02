package com.example.cc.testConfig;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import java.io.IOException;

@Slf4j
@Profile("local") // profile이 local일 때만 활성화
@Configuration
public class EmbeddedRedisConfig {

    private RedisServer redisServer;
    private int redisPort;

    @Value("${spring.data.redis.port}")
    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort; // @Value로 주입받은 포트 값을 필드에 설정
    }

    @PostConstruct
    public void startRedis() throws IOException {
        redisServer = new RedisServer(redisPort);
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        if(redisServer != null) {
            redisServer.stop();
        }
    }
}
