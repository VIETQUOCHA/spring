package com.example.demo3.service;

import com.example.demo3.entity.User;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
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
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header,payload);
        jwsObject.sign(new MACSigner(secretKey));
        return jwsObject.serialize();
    }

    public String generateRefreshToken(User user) throws JOSEException {
        Date issueTime = new Date();
        Date expiredTime = Date.from(issueTime.toInstant().plus(30,ChronoUnit.DAYS));
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .subject(user.getEmail())
                .issueTime(issueTime)
                .expirationTime(expiredTime)
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader,payload);
        jwsObject.sign(new MACSigner(secretKey));
        return jwsObject.serialize();
    }
}
