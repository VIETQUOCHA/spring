package com.example.demo.service;

import com.example.demo.DTO.*;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.CookieUtil;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String,String>redisTemplate;
    private final BlacklistService blacklistService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final CookieUtil cookieUtil;
    public EntityResponse<LoginResponse> login(LoginRequest request, HttpServletResponse response) throws JOSEException {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        User user = (User) authentication.getPrincipal();
        assert user != null;
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        //Set cookies
        Cookie accessCookie = new Cookie("access_token",accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(15 * 60);
        accessCookie.setAttribute("SameSite","Lax");

        Cookie refreshCookie = new Cookie("refresh_token",refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/api/auth");
        refreshCookie.setMaxAge(15 * 24 * 60 * 60);
        refreshCookie.setAttribute("SameSite","Strict");

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

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
    public EntityResponse<String> logout(HttpServletRequest request, HttpServletResponse response) throws ParseException {
       String accessToken = cookieUtil.extractCookieValue(request,"access_token");

       SignedJWT signedJWT = SignedJWT.parse(accessToken);
       JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
       String jwtId = claimsSet.getJWTID();
       String email = claimsSet.getSubject();
       Date expiration = claimsSet.getExpirationTime();
       if(jwtId == null){
           throw new RuntimeException("invalid token");
       }
       blacklistService.blacklist(jwtId,expiration);
       redisTemplate.delete("blacklist:refresh:email:"+ email);

       //delete token in cookie
        Cookie deleteAccess = new Cookie("access_token", null);
        deleteAccess.setPath("/");
        deleteAccess.setHttpOnly(true);
        deleteAccess.setSecure(true);
        deleteAccess.setMaxAge(0);
        deleteAccess.setAttribute("SameSite", "Lax");

        Cookie deleteRefresh = new Cookie("refresh_token", null);
        deleteRefresh.setPath("/api/auth/refresh");
        deleteRefresh.setHttpOnly(true);
        deleteRefresh.setSecure(true);
        deleteRefresh.setMaxAge(0);
        deleteRefresh.setAttribute("SameSite", "Strict");

        response.addCookie(deleteAccess);
        response.addCookie(deleteRefresh);

       return EntityResponse.<String>builder()
               .code(200)
               .message("logged")
               .result("invoke: " + jwtId)
               .build();
    }

    public EntityResponse<RefreshResponse>refresh(HttpServletRequest request, HttpServletResponse response) throws ParseException, JOSEException {
        String refreshToken = cookieUtil.extractCookieValue(request,"refresh_token");
        if(refreshToken == null){
            throw new JwtException("token invalid");
        }
        SignedJWT signedJWT = SignedJWT.parse(refreshToken);
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        String email = claimsSet.getSubject();
        Date expiration = claimsSet.getExpirationTime();
        long ttl = (expiration.getTime() - System.currentTimeMillis()) / 1000;
        if(ttl <= 0){
            throw new RuntimeException("token expired");
        }

        redisTemplate.delete("blacklist:refresh:email:"+ email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found"));
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        //Set cookies
        Cookie accessCookie = new Cookie("access_token",newAccessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(15 * 60);
        accessCookie.setAttribute("SameSite","Lax");

        Cookie refreshCookie = new Cookie("refresh_token",newRefreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/api/auth");
        refreshCookie.setMaxAge(15 * 24 * 60 * 60);
        refreshCookie.setAttribute("SameSite","Strict");

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

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
