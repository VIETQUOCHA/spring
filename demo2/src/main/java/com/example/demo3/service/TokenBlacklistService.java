package com.example.demo3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private static final String BLACKLIST_PREFIX= "blacklist:jti:";
    private final RedisTemplate<String,String>redisTemplate;

    public void blacklist (String jti, Date expiration){
        if(jti == null || expiration == null){
            return;
        }
        long ttlSeconds = (expiration.getTime() - System.currentTimeMillis()) / 1000;
        if(ttlSeconds > 0){
                redisTemplate.opsForValue().set(BLACKLIST_PREFIX + jti,"revoke",ttlSeconds, TimeUnit.SECONDS);
        }
    }

    public boolean isBlacklisted(String jti){
        if(jti == null){
            return false;
        }
        Boolean hasKey = redisTemplate.hasKey(BLACKLIST_PREFIX+ jti);
        return Boolean.TRUE.equals(hasKey);
    }

}
