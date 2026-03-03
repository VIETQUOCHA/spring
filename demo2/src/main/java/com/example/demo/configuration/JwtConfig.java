package com.example.demo.configuration;

import com.example.demo.service.BlacklistService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Bean
    public SecretKey secretKey(){
        byte[] keyByte = secretKey.getBytes(StandardCharsets.UTF_8);
        if(keyByte.length < 64){
            throw new RuntimeException("key must be bigger or equal 64 byte");
        }
        return new SecretKeySpec(keyByte,"HS512");
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey, BlacklistService blacklistService){
        NimbusJwtDecoder nimbusJwtDecoder =  NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
        return new BlacklistDecoder(blacklistService,nimbusJwtDecoder);
    }
}
