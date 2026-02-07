package com.example.demo.service;

import com.example.demo.DTO.EntityResponse;
import com.example.demo.DTO.UserRequest;
import com.example.demo.DTO.UserResponse;
import com.example.demo.DTO.UserUpdateRequest;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public EntityResponse<UserResponse> create(UserRequest request){
        User user = userMapper.toUser(request);
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("exists email");
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);
        UserResponse userResponse = userMapper.toUserResponse(user);
        return EntityResponse.<UserResponse>builder()
                .code(200)
                .message("creation success")
                .result(userResponse)
                .build();
    }

    public EntityResponse<Void> delete(String id){
        User user = userRepository.findById(id).orElseThrow(()-> new RuntimeException("not found user by id: " + id));
        userRepository.delete(user);
        return EntityResponse.<Void>builder()
                .code(333)
                .message("deleted id: " + id)
                .result(null)
                .build();
    }

    public EntityResponse<List<UserResponse>> users(){
        List<UserResponse> listUserResponse = userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
        return EntityResponse.<List<UserResponse>>builder()
                .code(500)
                .message("get users success")
                .result(listUserResponse)
                .build();
    }

    public EntityResponse<UserResponse> updateUser(String id, UserUpdateRequest request){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.toUpdateUser(request,user);
        userRepository.save(user);
        UserResponse userResponse = userMapper.toUserResponse(user);
        return EntityResponse.<UserResponse>builder()
                .code(111)
                .message("update success")
                .result(userResponse)
                .build();
    }
}
