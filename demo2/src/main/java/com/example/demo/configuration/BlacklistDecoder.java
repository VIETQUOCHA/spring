package com.example.demo.configuration;

import com.example.demo.service.BlacklistService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.text.ParseException;

public class BlacklistDecoder implements JwtDecoder {
    private final BlacklistService blacklistService;
    private final NimbusJwtDecoder nimbusJwtDecoder;

    public BlacklistDecoder(BlacklistService blacklistService, NimbusJwtDecoder nimbusJwtDecoder) {
        this.blacklistService = blacklistService;
        this.nimbusJwtDecoder = nimbusJwtDecoder;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            String jwtId = claimsSet.getJWTID();
            if (blacklistService.isBlacklist(jwtId)) {
                throw new RuntimeException("user logged");
            }
            return nimbusJwtDecoder.decode(token);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
