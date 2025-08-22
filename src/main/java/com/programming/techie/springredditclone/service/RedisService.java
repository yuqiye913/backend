package com.programming.techie.springredditclone.service;

import java.util.concurrent.TimeUnit;

public interface RedisService {
    
    void set(String key, Object value);
    
    void set(String key, Object value, long timeout, TimeUnit unit);
    
    <T> T get(String key, Class<T> clazz);
    
    Boolean delete(String key);
    
    Boolean hasKey(String key);
    
    Long getExpire(String key, TimeUnit unit);
    
    Boolean expire(String key, long timeout, TimeUnit unit);
}
