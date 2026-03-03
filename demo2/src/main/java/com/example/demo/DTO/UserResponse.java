package com.example.demo.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private int age;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
