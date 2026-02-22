package com.example.demo3.service;

import com.example.demo3.DTO.EntityResponse;
import com.example.demo3.DTO.UserRequest;
import com.example.demo3.DTO.UserResponse;
import com.example.demo3.DTO.UserUpdateRequest;
import com.example.demo3.entity.User;
import com.example.demo3.mapper.UserMapper;
import com.example.demo3.repository.UserRepository;
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
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("email already in use");
        }
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        UserResponse userResponse = userMapper.toUserResponse(user);
        return EntityResponse.<UserResponse>builder()
                .code(200)
                .message("user creating success")
                .result(userResponse)
                .build();
    }

    public EntityResponse<Void> delete(String id){
        User user = userRepository.findById(id).orElseThrow(()->new RuntimeException("not found id: " + id));
        userRepository.delete(user);
        return EntityResponse.<Void>builder()
                .code(201)
                .message("delete id: " + id)
                .result(null)
                .build();
    }

    public EntityResponse<UserResponse> update(String id, UserUpdateRequest request){
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("can not found id: " +id));
        userMapper.updateUser(request,user);
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);
        UserResponse userResponse = userMapper.toUserResponse(user);
        return EntityResponse.<UserResponse>builder()
                .code(202)
                .message("update success")
                .result(userResponse)
                .build();
    }

    public EntityResponse<List<UserResponse>>users(){
        List<UserResponse> responseList = userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
        return EntityResponse.<List<UserResponse>>builder()
                .code(204)
                .message("getting success")
                .result(responseList)
                .build();
    }
}
