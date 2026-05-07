package com.exam.common.security;

import com.exam.common.constant.SecurityConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisTokenStore {

    private final StringRedisTemplate redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    public RedisTokenStore(StringRedisTemplate redisTemplate, JwtTokenProvider jwtTokenProvider) {
        this.redisTemplate = redisTemplate;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void addToBlacklist(String jti, long expiration) {
        String key = SecurityConstants.TOKEN_BLACKLIST_PREFIX + jti;
        long ttl = Math.max(1, expiration - System.currentTimeMillis());
        redisTemplate.opsForValue().set(key, "1", ttl, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(SecurityConstants.TOKEN_BLACKLIST_PREFIX + jti));
    }

    public void cacheLoginUser(Long userId, String userJson) {
        redisTemplate.opsForValue().set(
                SecurityConstants.LOGIN_USER_KEY + userId, userJson,
                jwtTokenProvider.getAccessTokenExpire(), TimeUnit.MILLISECONDS);
    }

    public void removeLoginUser(Long userId) {
        redisTemplate.delete(SecurityConstants.LOGIN_USER_KEY + userId);
    }
}
