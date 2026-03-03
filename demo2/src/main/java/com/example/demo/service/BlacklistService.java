package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BlacklistService {
    private final RedisTemplate<String,String> redisTemplate;
    public void blacklist(String jwtId, Date expiration){
        if(jwtId == null || expiration == null){
            throw new RuntimeException("invalid token");
        }
        long ttlToken = (expiration.getTime() - System.currentTimeMillis()) / 1000;
        if(ttlToken > 0){
            redisTemplate.opsForValue()
                    .set("blacklist:access:jti" + jwtId,"revoke",ttlToken, TimeUnit.SECONDS);
        }
    }
    public Boolean isBlacklist(String jwtId){
        if(jwtId == null){
            return false;
        }
        Boolean hasKey = redisTemplate.hasKey("blacklist:access:jti" + jwtId);
        return Boolean.TRUE.equals(hasKey);
    }
}
