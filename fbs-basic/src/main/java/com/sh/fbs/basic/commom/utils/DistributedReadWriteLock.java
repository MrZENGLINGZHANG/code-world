package com.sh.fbs.basic.commom.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Distributed read-write lock implementation using Redis
 * Uses integer value to represent state:
 * - -1: Exclusive lock (Write lock) for cache miss and update
 * - 0: Shared state (Read state) for cache hit
 */
@Slf4j
public class DistributedReadWriteLock {
    private final RedisTemplate<String, String> redisTemplate;
    private final String lockKey;
    private final long expireSeconds;
    
    private static final String EXCLUSIVE_LOCK = "-1";
    private static final String SHARED_STATE = "0";

    // Lua脚本：如果key不存在或者不是共享状态，尝试设置独占锁
    private static final String TRY_LOCK_SCRIPT = 
            "local current = redis.call('get', KEYS[1]); " +
            "if (not current or current ~= ARGV[2]) then " +
            "  redis.call('set', KEYS[1], ARGV[1], 'EX', ARGV[3]); " +
            "  return 1; " +
            "elseif current == ARGV[2] then " +
            "  return 0; " +
            "else " +
            "  return -1; " +
            "end;";

    public DistributedReadWriteLock(RedisTemplate<String, String> redisTemplate, String lockKey, long expireSeconds) {
        this.redisTemplate = redisTemplate;
        this.lockKey = lockKey;
        this.expireSeconds = expireSeconds;
    }

    /**
     * Try to acquire lock
     * Returns:
     * 1: Got exclusive lock
     * 0: Already in shared state
     * -1: Failed to get lock
     */
    public int tryLock() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(TRY_LOCK_SCRIPT, Long.class);
        Long result = redisTemplate.execute(script,
                Collections.singletonList(lockKey),
                EXCLUSIVE_LOCK, SHARED_STATE, String.valueOf(expireSeconds));
        
        return result != null ? result.intValue() : -1;
    }

    /**
     * Upgrade to shared state by setting value to 0
     */
    public void upgradeToShared() {
        redisTemplate.opsForValue().set(lockKey, SHARED_STATE, expireSeconds, TimeUnit.SECONDS);
    }

    /**
     * Release lock
     */
    public void release() {
        redisTemplate.delete(lockKey);
    }
} 