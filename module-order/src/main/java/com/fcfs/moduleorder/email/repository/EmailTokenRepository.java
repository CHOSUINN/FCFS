package com.fcfs.moduleorder.email.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class EmailTokenRepository {

    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "emailVerify";

    public void saveToken(String token, String userId, Duration ttl) {
        String key = PREFIX + token;
        redisTemplate.opsForValue()
                .set(key, userId, ttl);
    }

    public String getUserIdByToken(String token) {
        return redisTemplate.opsForValue()
                .get(PREFIX + token);
    }

    public void deleteToken(String token) {
        redisTemplate.delete(PREFIX + token);
    }
}
