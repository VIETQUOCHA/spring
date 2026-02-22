package com.example.demo3.controller;

import com.example.demo3.DTO.EntityResponse;
import com.example.demo3.DTO.LoginRequest;
import com.example.demo3.DTO.LoginResponse;
import com.example.demo3.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/login")
    public EntityResponse<LoginResponse>login(@RequestBody LoginRequest request) throws JOSEException {
        return authenticationService.login(request);
    }

    @PostMapping("/api/auth/logout")
    public EntityResponse<String>logout(@RequestHeader("Authorization") String authorizationHeader){
        return authenticationService.logout(authorizationHeader);
    }
}
