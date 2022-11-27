package com.jjbacsa.jjbacsabackend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate stringRedisTemplate;

    public String getStringValue(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void setStringValue(String key, String value){
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void setToken(String key, String token) {
        stringRedisTemplate.opsForValue().set(key, token, 14, TimeUnit.DAYS);
    }

    public void deleteValue(String key){
        stringRedisTemplate.delete(key);
    }
}
