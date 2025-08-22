package com.programming.techie.springredditclone.service.impl;

import com.programming.techie.springredditclone.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            log.debug("Redis: Set key '{}' with value: {}", key, value);
        } catch (Exception e) {
            log.error("Redis: Error setting key '{}': {}", key, e.getMessage());
        }
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            log.debug("Redis: Set key '{}' with value: {} and TTL: {} {}", key, value, timeout, unit);
        } catch (Exception e) {
            log.error("Redis: Error setting key '{}' with TTL: {}", key, e.getMessage());
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                log.debug("Redis: Retrieved key '{}' with value: {}", key, value);
                return clazz.cast(value);
            }
            log.debug("Redis: Key '{}' not found", key);
            return null;
        } catch (Exception e) {
            log.error("Redis: Error getting key '{}': {}", key, e.getMessage());
            return null;
        }
    }

    @Override
    public Boolean delete(String key) {
        try {
            Boolean result = redisTemplate.delete(key);
            log.debug("Redis: Deleted key '{}': {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("Redis: Error deleting key '{}': {}", key, e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean hasKey(String key) {
        try {
            Boolean result = redisTemplate.hasKey(key);
            log.debug("Redis: Has key '{}': {}", key, result);
            return result;
        } catch (Exception e) {
            log.error("Redis: Error checking key '{}': {}", key, e.getMessage());
            return false;
        }
    }

    @Override
    public Long getExpire(String key, TimeUnit unit) {
        try {
            Long result = redisTemplate.getExpire(key, unit);
            log.debug("Redis: Get expire for key '{}': {} {}", key, result, unit);
            return result;
        } catch (Exception e) {
            log.error("Redis: Error getting expire for key '{}': {}", key, e.getMessage());
            return null;
        }
    }

    @Override
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            Boolean result = redisTemplate.expire(key, timeout, unit);
            log.debug("Redis: Set expire for key '{}': {} {} - {}", key, timeout, unit, result);
            return result;
        } catch (Exception e) {
            log.error("Redis: Error setting expire for key '{}': {}", key, e.getMessage());
            return false;
        }
    }
}
