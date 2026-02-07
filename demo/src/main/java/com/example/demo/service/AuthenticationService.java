package com.example.demo.service;

import com.example.demo.DTO.EntityResponse;
import com.example.demo.DTO.LoginRequest;
import com.example.demo.DTO.LoginResponse;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    public EntityResponse<LoginResponse> login(LoginRequest request){
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken("111111")
                .refreshToken("222222")
                .build();
        return EntityResponse.<LoginResponse>builder()
                .code(500)
                .message("created token")
                .result(loginResponse)
                .build();

    }
}
