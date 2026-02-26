package com.example.demo.controller;

import com.example.demo.DTO.*;
import com.example.demo.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    @PostMapping("/login")
    public EntityResponse<LoginResponse> login(@RequestBody LoginRequest request) throws JOSEException {
        return authenticationService.login(request);
    }
    @PostMapping("/api/auth/logout")
    public EntityResponse<String>logout(@RequestHeader("Authorization")String authorizationHeader) throws ParseException {
        return authenticationService.logout(authorizationHeader);
    }
    @PostMapping("/api/auth/refresh")
    public EntityResponse<RefreshResponse>refresh(@RequestBody RefreshRequest request) throws ParseException, JOSEException {
        return authenticationService.refresh(request);
    }
}
