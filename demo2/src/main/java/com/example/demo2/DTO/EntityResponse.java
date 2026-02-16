package com.example.demo2.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntityResponse<T> {
    private int code;
    private String message;
    private T result;
}
