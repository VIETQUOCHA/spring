package com.example.demo3.configuration;

import com.example.demo3.service.TokenBlacklistService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.oauth2.jwt.*;

import java.text.ParseException;

public class BlacklistAwareJwtDecoder implements JwtDecoder {
    private final NimbusJwtDecoder nimbusJwtDecoder;
    private final TokenBlacklistService tokenBlacklistService;
    public BlacklistAwareJwtDecoder(NimbusJwtDecoder nimbusJwtDecoder, TokenBlacklistService tokenBlacklistService) {
        this.nimbusJwtDecoder = nimbusJwtDecoder;
        this.tokenBlacklistService = tokenBlacklistService;
    }
    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            String jti = claimsSet.getJWTID();
            if(tokenBlacklistService.isBlacklisted(jti)){
                throw new BadJwtException("Token has been revoked (blacklisted)");
            }
            return nimbusJwtDecoder.decode(token);
        } catch (ParseException e) {
            throw new BadJwtException("Invalid JWT format", e);
        }
    }
}
