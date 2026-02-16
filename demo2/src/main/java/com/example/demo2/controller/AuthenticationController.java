package com.example.demo2.controller;

import com.example.demo2.DTO.EntityResponse;
import com.example.demo2.DTO.LoginRequest;
import com.example.demo2.DTO.LoginResponse;
import com.example.demo2.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/login")
    public EntityResponse<LoginResponse>login(@RequestBody LoginRequest request) throws JOSEException {
        return authenticationService.login(request);
    }
}
