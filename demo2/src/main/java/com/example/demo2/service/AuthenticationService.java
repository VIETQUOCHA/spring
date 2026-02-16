package com.example.demo2.service;

import com.example.demo2.DTO.EntityResponse;
import com.example.demo2.DTO.LoginRequest;
import com.example.demo2.DTO.LoginResponse;
import com.example.demo2.entity.User;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
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
}
