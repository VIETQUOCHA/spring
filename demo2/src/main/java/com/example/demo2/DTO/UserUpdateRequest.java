package com.example.demo2.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {
    private String username;
    private String email;
    private String password;
    private Integer age;
}
