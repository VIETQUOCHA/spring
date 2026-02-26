package com.example.demo.service;

import com.example.demo.entity.User;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final RedisTemplate<String,String>redisTemplate;
    @Value("${jwt.secret-key}")
    private String secretKey;
    public String generateToken(User user) throws JOSEException {
        Date issueTime = new Date();
        Date expiredTime = Date.from(issueTime.toInstant().plus(15, ChronoUnit.MINUTES));
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .subject(user.getEmail())
                .issueTime(issueTime)
                .expirationTime(expiredTime)
                .claim("type","access")
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header,payload);
        jwsObject.sign(new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8)));
        return jwsObject.serialize();
    }

    public String generateRefreshToken(User user) throws JOSEException {
        Date issueTime = new Date();
        Date expiredTime = Date.from(issueTime.toInstant().plus(15, ChronoUnit.DAYS));
        String jwtId = UUID.randomUUID().toString();
        String email = user.getEmail();
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(jwtId)
                .subject(email)
                .issueTime(issueTime)
                .expirationTime(expiredTime)
                .claim("type","refresh")
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header,payload);
        jwsObject.sign(new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8)));
        String token = jwsObject.serialize();
        String hashToken = DigestUtils.sha1DigestAsHex(token);
        long ttlToken = (expiredTime.getTime() - System.currentTimeMillis()) /1000;
        redisTemplate.opsForValue().set("blacklist:refresh:email:"+ email,hashToken,ttlToken, TimeUnit.SECONDS);
        return token;
    }

}
