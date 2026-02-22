package com.example.demo3.service;

import com.example.demo3.DTO.EntityResponse;
import com.example.demo3.DTO.LoginRequest;
import com.example.demo3.DTO.LoginResponse;
import com.example.demo3.entity.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final TokenBlacklistService blacklistService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public EntityResponse<LoginResponse>login(LoginRequest request) throws JOSEException {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword());
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
                .code(205)
                .message("login success")
                .result(loginResponse)
                .build();
    }

    public EntityResponse<String>logout(String authorizationHeader){
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return EntityResponse.<String>builder()
                    .code(400)
                    .message("No token provided")
                    .build();
        }
        String token = authorizationHeader.substring(7);
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            String jti = claimsSet.getJWTID();
            blacklistService.blacklist(jti,claimsSet.getExpirationTime());
            return EntityResponse.<String>builder()
                    .code(200)
                    .message("Logout successful")
                    .result("User logged out")
                    .build();
        } catch (ParseException e) {
            return EntityResponse.<String>builder()
                    .code(401)
                    .message("Invalid token")
                    .build();
        }
    }
}
