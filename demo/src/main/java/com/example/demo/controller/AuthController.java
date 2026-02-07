package com.example.demo.controller;

import com.example.demo.DTO.EntityResponse;
import com.example.demo.DTO.LoginRequest;
import com.example.demo.DTO.LoginResponse;
import com.example.demo.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    @PostMapping("/login")
    public EntityResponse<LoginResponse> login(@RequestBody LoginRequest request){
        return authenticationService.login(request);
    }
}
