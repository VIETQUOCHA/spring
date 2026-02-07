package com.example.demo.DTO;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {
    private String username;
    private String email;
    private int age;
    private String password;
}
