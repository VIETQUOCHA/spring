package com.example.demo3.controller;

import com.example.demo3.DTO.EntityResponse;
import com.example.demo3.DTO.UserRequest;
import com.example.demo3.DTO.UserResponse;
import com.example.demo3.DTO.UserUpdateRequest;
import com.example.demo3.service.UserService;
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

    @PutMapping("/update/{id}")
    public EntityResponse<UserResponse>update(@PathVariable String id, @RequestBody UserUpdateRequest request){
        return userService.update(id, request);
    }

    @GetMapping("/users")
    public EntityResponse<List<UserResponse>>users(){
        return userService.users();
    }
}
