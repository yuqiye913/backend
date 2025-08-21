package com.programming.techie.springredditclone.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @Test
    public void testRedisOperations() {
        String key = "test:key";
        String value = "test value";

        // Test set and get
        redisService.set(key, value);
        String retrievedValue = redisService.get(key, String.class);
        assertEquals(value, retrievedValue);

        // Test hasKey
        assertTrue(redisService.hasKey(key));

        // Test set with TTL
        String ttlKey = "test:ttl:key";
        redisService.set(ttlKey, value, 1, TimeUnit.SECONDS);
        assertTrue(redisService.hasKey(ttlKey));

        // Test delete
        redisService.delete(key);
        assertFalse(redisService.hasKey(key));
    }
}
