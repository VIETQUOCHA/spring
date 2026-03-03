package com.example.demo.DTO;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntityResponse<T> {
    private int code;
    private String message;
    private T result;
}
