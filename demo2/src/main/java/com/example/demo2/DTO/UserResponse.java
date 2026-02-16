package com.example.demo2.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private Integer age;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
