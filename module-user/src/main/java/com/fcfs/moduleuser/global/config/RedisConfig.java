package com.fcfs.moduleuser.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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

    // opsForList(): Redis의 List 자료구조(예: LPUSH, RPUSH, LPOP, LRANGE 등) 관련 메서드를 제공
    public ListOperations<String, Object> getListOperations() {
        return this.redisTemplate().opsForList();
    }

    // opsForValue(): Redis의 String(key‐value) 자료구조(예: GET, SET, INCR, DECR 등) 관련 메서드를 제공
    public ValueOperations<String, Object> getValueOperations() {
        return this.redisTemplate().opsForValue();
    }

    // Runnable 인터페이스를 인자로 받아서, 내부에서 operation.run()을 실행합니다.
    public int executeOperation(Runnable operation) {
        try {
            operation.run();
            return 1;
        } catch (Exception e) {
            System.out.println("Redis 작업 오류 발생 :: " + e.getMessage());
            return 0;
        }
    }
}
