package org.example.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: RedisCache
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/22 17:14
 **/
@Component
public class RedisCache {

    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedisCache(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void set(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    public void delete(Set<String> key) {
        stringRedisTemplate.delete(key);
    }
}

