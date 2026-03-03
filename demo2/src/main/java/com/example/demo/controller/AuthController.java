package com.example.demo.controller;

import com.example.demo.DTO.*;
import com.example.demo.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public EntityResponse<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) throws JOSEException {
        return authenticationService.login(request,response);
    }
    @PostMapping("/api/auth/logout")
    public EntityResponse<String>logout(HttpServletRequest request, HttpServletResponse response) throws ParseException {
        return authenticationService.logout(request,response);
    }
    @PostMapping("/api/auth/refresh")
    public EntityResponse<RefreshResponse>refresh(HttpServletRequest request, HttpServletResponse response) throws ParseException, JOSEException {
        return authenticationService.refresh(request,response);
    }
}
