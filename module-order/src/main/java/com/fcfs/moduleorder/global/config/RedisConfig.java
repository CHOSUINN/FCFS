package com.fcfs.moduleorder.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    // redis 서버와의 연결 팩토리 생성하는 메소드. RedisConnectionFactory를 통해 redis 서버와의 커넥션(소켓 연결)을 관리한다.
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Lettuce는 논블로킹(Non-Blocking) IO 기반 클라이언트이다.
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // redis 연결
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // key-value 형태로 직렬화 수행
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // Hash Key-Value 형태로 직렬화 수행
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        // 기본적으로 직렬화 수행
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
