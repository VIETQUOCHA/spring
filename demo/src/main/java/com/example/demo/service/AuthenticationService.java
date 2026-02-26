package com.example.demo.service;

import com.example.demo.DTO.*;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String,String>redisTemplate;
    private final BlacklistService blacklistService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    public EntityResponse<LoginResponse> login(LoginRequest request) throws JOSEException {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        User user = (User) authentication.getPrincipal();
        assert user != null;
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        return EntityResponse.<LoginResponse>builder()
                .code(500)
                .message("created token")
                .result(loginResponse)
                .build();

    }
    public EntityResponse<String> logout(String authorizationHeader) throws ParseException {
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            throw  new RuntimeException("token invalid");
        }
        String token = authorizationHeader.substring(7);
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        String jwtId = claimsSet.getJWTID();
        String email = claimsSet.getSubject();
        Date expiration = claimsSet.getExpirationTime();
        blacklistService.blacklist(jwtId,expiration);
        redisTemplate.delete("blacklist:refresh:email:"+ email);
        return EntityResponse.<String>builder()
                .code(200)
                .message("logged")
                .result("blacklist:access:jti" + jwtId)
                .build();
    }

    public EntityResponse<RefreshResponse>refresh(RefreshRequest request) throws ParseException, JOSEException {
        if(request == null){
           throw new RuntimeException("invalid token");
        }
        SignedJWT signedJWT = SignedJWT.parse(request.getRefreshToken());
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        Date expiration = claimsSet.getExpirationTime();
        String email = claimsSet.getSubject();
        if(expiration.before(new Date())){
            throw new RuntimeException("token expiration");
        }
        String hashTokenRequest = DigestUtils.sha1DigestAsHex(request.getRefreshToken());
        String hashTokenBlacklist = redisTemplate.opsForValue().get("blacklist:refresh:email:"+ email);
        if(!Objects.equals(hashTokenBlacklist, hashTokenRequest)){
            throw new RuntimeException("invalid token");
        }
        redisTemplate.delete("blacklist:refresh:email:"+ email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found"));
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        RefreshResponse refreshResponse = RefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
        return EntityResponse.<RefreshResponse>builder()
                .code(200)
                .message("refresh")
                .result(refreshResponse)
                .build();
    }
}
