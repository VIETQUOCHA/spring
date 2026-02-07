package com.example.demo.controller;

import com.example.demo.DTO.EntityResponse;
import com.example.demo.DTO.UserRequest;
import com.example.demo.DTO.UserResponse;
import com.example.demo.DTO.UserUpdateRequest;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public EntityResponse<UserResponse> create(@RequestBody UserRequest request){
        return userService.create(request);
    }

    @DeleteMapping("/delete/{id}")
    public EntityResponse<Void> delete(@PathVariable String id){
        return userService.delete(id);
    }

    @GetMapping("/users")
    public EntityResponse<List<UserResponse>> users(){
        return userService.users();
    }

    @PutMapping("/update/{id}")
    public EntityResponse<UserResponse> update(@PathVariable String id,@RequestBody UserUpdateRequest request){
        return userService.updateUser(id, request);
    }
}
